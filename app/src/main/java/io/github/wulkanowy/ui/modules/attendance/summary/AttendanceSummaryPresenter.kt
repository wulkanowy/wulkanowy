package io.github.wulkanowy.ui.modules.attendance.summary

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.data.repositories.SubjectRepostory
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.calculateAttendance
import io.github.wulkanowy.utils.logEvent
import java.lang.String.format
import java.util.Locale
import javax.inject.Inject

class AttendanceSummaryPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val subjectRepository: SubjectRepostory,
    private val sessionRepository: SessionRepository,
    private val schedulers: SchedulersProvider
) : BasePresenter<AttendanceSummaryView>(errorHandler) {

    private var subjects: List<Subject> = emptyList()

    var currentSubjectId: Int = -1
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

    fun loadForSubject(name: String) {
        loadData(subjects.singleOrNull { it.name == name }?.realId ?: -1)
    }

    private fun loadData(subjectId: Int, forceRefresh: Boolean = false) {
        currentSubjectId = subjectId
        disposable.apply {
            clear()
            add(sessionRepository.getSemesters()
                .map { it.single { semester -> semester.current } }
                .flatMap { attendanceSummaryRepository.getAttendanceSummary(it, subjectId, forceRefresh) }
                .map { createAttendanceSummaryItems(it) to AttendanceSummaryScrollableHeader(formatAttendance(it.calculateAttendance())) }
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
                    errorHandler.proceed(it)
                }
            )
        }
    }

    private fun createAttendanceSummaryItems(attendanceSummary: List<AttendanceSummary>): List<AttendanceSummaryItem> {
        return attendanceSummary.sortedByDescending { it.id }.flatMap { summary ->
            AttendanceSummaryHeader(
                name = summary.month,
                value = formatAttendance(summary.calculateAttendance())
            ).let {
                listOf(
                    AttendanceSummaryItem(
                        header = it,
                        name = "Obecność",
                        value = summary.presence
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Nieobecność",
                        value = summary.absence
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Nieobecność usprawiedliwiona",
                        value = summary.absenceExcused
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Nieobecność z przyczyn szkolnych",
                        value = summary.absenceForSchoolReasons
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Zwolnienie",
                        value = summary.exemption
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Spóźnienie",
                        value = summary.lateness
                    ),
                    AttendanceSummaryItem(
                        header = it,
                        name = "Spóźnienie usprawiedliwione",
                        value = summary.latenessExcused
                    )
                )
            }
        }
    }

    private fun formatAttendance(attendance: Double, defaultValue: String = "0%"): String {
        return if (attendance == 0.0) defaultValue
        else format(Locale.FRANCE, "%.2f", attendance) + "%"
    }

    private fun loadSubjects() {
        disposable.add(sessionRepository.getSemesters()
            .map { it.single { semester -> semester.current } }
            .flatMap { subjectRepository.getSubjects(it) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                subjects = it
                view?.updateSubjects(ArrayList(it.map { subject -> subject.name }))
            }, {
                view?.run { showEmpty(isViewEmpty) }
                errorHandler.proceed(it)
            })
        )
    }
}
