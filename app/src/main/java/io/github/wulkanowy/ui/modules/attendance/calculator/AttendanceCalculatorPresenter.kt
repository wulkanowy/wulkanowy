package io.github.wulkanowy.ui.modules.attendance.calculator

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.domain.attendance.GetAttendanceCalculatorDataUseCase
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class AttendanceCalculatorPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository,
    private val getAttendanceCalculatorData: GetAttendanceCalculatorDataUseCase,
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
            getAttendanceCalculatorData(student, semester, forceRefresh)
        }
            .logResourceStatus("load attendance calculator")
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
