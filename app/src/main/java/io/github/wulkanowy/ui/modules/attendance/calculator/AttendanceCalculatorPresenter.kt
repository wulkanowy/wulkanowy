package io.github.wulkanowy.ui.modules.attendance.calculator

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.allAbsences
import io.github.wulkanowy.utils.allPresences
import io.github.wulkanowy.utils.calculatePercentage
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.Month
import javax.inject.Inject

class AttendanceCalculatorPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val preferencesRepository: PreferencesRepository,
) : BasePresenter<AttendanceCalculatorView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: AttendanceCalculatorView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Attendance calculator view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the attendance calculator")
        loadData(forceRefresh = true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        preferencesRepository.targetAttendanceFlow.onEach {
            view?.setTargetAttendance(it.toDouble() / 100)
        }.launch("targetAttendance")

        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            attendanceSummaryRepository.getSumAttendanceSummaryWithName(student, semester, forceRefresh)
        }
            .logResourceStatus("load attendance calculator")
            .mapResourceData { it.map { (name, summaryList) -> name to summaryList.sum() }  }
            .mapResourceData { it.filter { (_, summary) -> summary.allAbsences > 0 || summary.allPresences > 0 } }
            .mapResourceData { it.sortedBy { (_, summary) -> summary.calculatePercentage() } }
            .onResourceData {
                view?.run {
                    showProgress(false)
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    updateData(it)
                }
            }
            .onResourceIntermediate { view?.showRefresh(true) }
            .onResourceNotLoading {
                view?.run {
                    enableSwipe(true)
                    showRefresh(false)
                    showProgress(false)
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch()
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
            } else showError(message, error)
        }
    }
}

fun List<AttendanceSummary>.sum() = AttendanceSummary(
    month = Month.APRIL,
    presence = this.sumOf { it.presence },
    absence = this.sumOf { it.absence },
    absenceExcused = this.sumOf { it.absenceExcused },
    absenceForSchoolReasons = this.sumOf { it.absenceForSchoolReasons },
    exemption = this.sumOf { it.exemption },
    lateness = this.sumOf { it.lateness },
    latenessExcused = this.sumOf { it.latenessExcused },
    diaryId = this.getOrNull(0)?.diaryId ?: -1,
    studentId = this.getOrNull(0)?.studentId ?: -1,
    subjectId = this.getOrNull(0)?.subjectId ?: -1
)
