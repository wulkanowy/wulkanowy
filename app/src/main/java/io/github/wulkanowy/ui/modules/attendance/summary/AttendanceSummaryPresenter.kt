package io.github.wulkanowy.ui.modules.attendance.summary

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.SubjectRepostory
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.calculatePercentage
import io.github.wulkanowy.utils.logEvent
import java.lang.String.format
import java.util.Locale.FRANCE
import javax.inject.Inject

class AttendanceSummaryPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val subjectRepository: SubjectRepostory,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schedulers: SchedulersProvider
) : BasePresenter<AttendanceSummaryView>(errorHandler) {

    private var subjects = emptyList<Subject>()

    var currentSubjectId = -1
        private set

    fun onAttachView(view: AttendanceSummaryView, subjectId: Int?) {
        super.onAttachView(view)
        view.initView()
        loadData(subjectId ?: -1)
        loadSubjects()
    }

    fun onSwipeRefresh() {
        loadData(currentSubjectId, true)
    }

    fun onSubjectSelected(name: String) {
        loadData(subjects.singleOrNull { it.name == name }?.realId ?: -1)
    }

    private fun loadData(subjectId: Int, forceRefresh: Boolean = false) {
        currentSubjectId = subjectId
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .flatMap { attendanceSummaryRepository.getAttendanceSummary(it, subjectId, forceRefresh) }
                .map { createAttendanceSummaryItems(it) to AttendanceSummaryScrollableHeader(formatPercentage(it.calculatePercentage())) }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doOnSubscribe {
                    view?.run {
                        showProgress(!forceRefresh)
                        showContent(forceRefresh)
                    }
                }
                .doFinally {
                    view?.run {
                        hideRefresh()
                        showProgress(false)
                    }
                }
                .subscribe({
                    view?.apply {
                        showEmpty(it.first.isEmpty())
                        showContent(it.first.isNotEmpty())
                        updateDataSet(it.first, it.second)
                    }
                    logEvent("Attendance load", mapOf("forceRefresh" to forceRefresh))
                }) {
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                }
            )
        }
    }

    private fun loadSubjects() {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { subjectRepository.getSubjects(it) }
            .doOnSuccess { subjects = it }
            .map { ArrayList(it.map { subject -> subject.name }) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ view?.updateSubjects(it) }, {
                view?.run { showEmpty(isViewEmpty) }
                errorHandler.dispatch(it)
            })
        )
    }

    private fun createAttendanceSummaryItems(attendanceSummary: List<AttendanceSummary>): List<AttendanceSummaryItem> {
        return attendanceSummary.sortedByDescending { it.id }.flatMap { summary ->
            AttendanceSummaryHeader(
                name = summary.month,
                value = formatPercentage(summary.calculatePercentage())
            ).let {
                listOf(
                    AttendanceSummaryItem(
                        header = it,
                        name = "Obecność",
                        value = summary.presence.toString()
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Nieobecność",
                        value = summary.absence.toString()
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Nieobecność usprawiedliwiona",
                        value = summary.absenceExcused.toString()
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Nieobecność z przyczyn szkolnych",
                        value = summary.absenceForSchoolReasons.toString()
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Zwolnienie",
                        value = summary.exemption.toString()
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Spóźnienie",
                        value = summary.lateness.toString()
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Spóźnienie usprawiedliwione",
                        value = summary.latenessExcused.toString()
                    )
                )
            }
        }
    }

    private fun formatPercentage(percentage: Double): String {
        return if (percentage == 0.0) "0%"
        else "${format(FRANCE, "%.2f", percentage)}%"
    }
}
