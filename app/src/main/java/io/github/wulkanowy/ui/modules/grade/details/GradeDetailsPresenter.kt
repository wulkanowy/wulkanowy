package io.github.wulkanowy.ui.modules.grade.details

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.changeModifier
import io.github.wulkanowy.utils.getBackgroundColor
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GradeDetailsPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val gradeRepository: GradeRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<GradeDetailsView>(errorHandler) {

    private var currentSemesterId = 0

    override fun onAttachView(view: GradeDetailsView) {
        super.onAttachView(view)
        view.initView()
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        currentSemesterId = semesterId
        loadData(semesterId, forceRefresh)
    }

    fun onGradeItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is GradeDetailsItem) {
            Timber.i("Select grade item ${item.grade.id}")
            view?.apply {
                showGradeDialog(item.grade, preferencesRepository.gradeColorTheme)
                if (!item.grade.isRead) {
                    item.grade.isRead = true
                    updateItem(item)
                    getHeaderOfItem(item)?.let { header ->
                        if (header is GradeDetailsHeader) {
                            header.newGrades--
                            updateItem(header)
                        }
                    }
                    updateGrade(item.grade)
                }
            }
        }
    }

    fun onMarkAsReadSelected(): Boolean {
        Timber.i("Select mark grades as read")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .flatMap { gradeRepository.getUnreadGrades(it.first { item -> item.semesterId == currentSemesterId }) }
            .map { it.map { grade -> grade.apply { isRead = true } } }
            .flatMapCompletable {
                Timber.i("Mark as read ${it.size} grades")
                gradeRepository.updateGrades(it)
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.i("Mark as read result: Success")
                loadData(currentSemesterId, false)
            }, {
                Timber.i("Mark as read result: An exception occurred")
                errorHandler.dispatch(it)
            }))
        return true
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the grade details")
        view?.notifyParentRefresh()
    }

    fun onParentViewReselected() {
        view?.run {
            if (!isViewEmpty) {
                if (preferencesRepository.isGradeExpandable) collapseAllItems()
                scrollToStart()
            }
        }
    }

    fun onParentViewChangeSemester() {
        view?.run {
            showProgress(true)
            enableSwipe(false)
            showRefresh(false)
            showContent(false)
            showEmpty(false)
            clearView()
        }
        disposable.clear()
    }

    private fun loadData(semesterId: Int, forceRefresh: Boolean) {
        Timber.i("Loading grade details data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it).map { semester -> it to semester } }
            .flatMap { (student, semesters) ->
                getModdedAverage(student, semesters, semesterId, forceRefresh)
                    .flatMap { averages ->
                        gradeRepository.getGrades(student, semesters.first { semester -> semester.semesterId == semesterId })
                            .map { it.sortedByDescending { grade -> grade.date } }
                            .map { it.groupBy { grade -> grade.subject }.toSortedMap() }
                            .map { createGradeItems(it, averages) }
                    }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    showRefresh(false)
                    showProgress(false)
                    enableSwipe(true)
                    notifyParentDataLoaded(semesterId)
                }
            }
            .subscribe({
                Timber.i("Loading grade details result: Success")
                view?.run {
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                    updateData(it)
                }
                analytics.logEvent("load_grade_details", "items" to it.size, "force_refresh" to forceRefresh)
            }) {
                Timber.i("Loading grade details result: An exception occurred")
                view?.run { showEmpty(isViewEmpty) }
                errorHandler.dispatch(it)
            })
    }

    private fun createGradeItems(items: Map<String, List<Grade>>, averages: Map<String, Double>): List<GradeDetailsHeader> {
        val isGradeExpandable = preferencesRepository.isGradeExpandable
        val gradeColorTheme = preferencesRepository.gradeColorTheme

        val noDescriptionString = view?.noDescriptionString.orEmpty()
        val weightString = view?.weightString.orEmpty()

        return items.map {
            GradeDetailsHeader(
                subject = it.key,
                average = formatAverage(averages[it.key]),
                number = view?.getGradeNumberString(it.value.size).orEmpty(),
                newGrades = it.value.filter { grade -> !grade.isRead }.size,
                isExpandable = isGradeExpandable
            ).apply {
                subItems = it.value.map { item ->
                    GradeDetailsItem(
                        grade = item,
                        valueBgColor = item.getBackgroundColor(gradeColorTheme),
                        weightString = weightString,
                        noDescriptionString = noDescriptionString
                    )
                }
            }
        }
    }

    private fun getOnlyOneSemesterAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<Map<String, Double>> {
        val selectedSemester = semesters.first { it.semesterId == semesterId }
        val plusModifier = preferencesRepository.gradePlusModifier
        val minusModifier = preferencesRepository.gradeMinusModifier

        return gradeRepository.getGrades(student, selectedSemester, forceRefresh)
            .map { grades ->
                grades.map { it.changeModifier(plusModifier, minusModifier) }
                    .groupBy { it.subject }
                    .mapValues { it.value.calcAverage() }
            }
    }

    private fun getAllYearAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<Map<String, Double>> {
        val selectedSemester = semesters.first { it.semesterId == semesterId }
        val firstSemester = semesters.first { it.diaryId == selectedSemester.diaryId && it.semesterName == 1 }
        val plusModifier = preferencesRepository.gradePlusModifier
        val minusModifier = preferencesRepository.gradeMinusModifier

        return gradeRepository.getGrades(student, selectedSemester, forceRefresh)
            .flatMap { firstGrades ->
                if (selectedSemester == firstSemester) Single.just(firstGrades)
                else gradeRepository.getGrades(student, firstSemester, forceRefresh)
                    .map { secondGrades -> secondGrades + firstGrades }
            }.map { grades ->
                grades.map { it.changeModifier(plusModifier, minusModifier) }
                    .groupBy { it.subject }
                    .mapValues { it.value.calcAverage() }
            }
    }

    private fun getModdedAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<Map<String, Double>> {
        return when (preferencesRepository.gradeAverageMode) {
            "all_year" -> getAllYearAverage(student, semesters, semesterId, forceRefresh)
            else -> getOnlyOneSemesterAverage(student, semesters, semesterId, forceRefresh)
        }
    }

    private fun formatAverage(average: Double?): String {
        return view?.run {
            if (average == null || average == .0) emptyAverageString
            else averageString.format(average)
        }.orEmpty()
    }

    private fun updateGrade(grade: Grade) {
        Timber.i("Attempt to update grade ${grade.id}")
        disposable.add(gradeRepository.updateGrade(grade)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ Timber.i("Update grade result: Success") })
            { error ->
                Timber.i("Update grade result: An exception occurred")
                errorHandler.dispatch(error)
            })
    }
}
