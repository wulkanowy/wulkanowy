package io.github.wulkanowy.ui.modules.luckynumber.history

import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class LuckyNumberHistoryPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val luckyNumberRepository: LuckyNumberRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<LuckyNumberHistoryView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    var currentDate: LocalDate = LocalDate.now().previousOrSameSchoolDay

    override fun onAttachView(view: LuckyNumberHistoryView) {
        super.onAttachView(view)
        view.run {
            initView()
            reloadNavigation()
            showContent(false)
        }
        Timber.i("Lucky number history view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
        if (currentDate.isHolidays) setBaseDateOnHolidays()
    }

    private fun setBaseDateOnHolidays() {
        flow {
            val student = studentRepository.getCurrentStudent()
            emit(semesterRepository.getCurrentSemester(student))
        }
            .catch { Timber.i("Loading semester result: An exception occurred") }
            .onEach {
                currentDate = currentDate.getLastSchoolDayIfHoliday(it.schoolYear)
                reloadNavigation()
            }
            .launch("holidays")
    }

    private fun loadData() {
        resourceFlow {
            val student = studentRepository.getCurrentStudent()
            luckyNumberRepository.getLuckyNumberHistory(
                student,
                currentDate.monday,
                currentDate.sunday
            )
        }
            .logResourceStatus("load lucky number history")
            .onResourceError(errorHandler::dispatch)
            .onResourceSuccess {
                val first = it.first()
                if (!first.isNullOrEmpty()) {
                    view?.apply {
                        updateData(first)
                        showContent(true)
                        showEmpty(false)
                        showErrorView(false)
                        showProgress(false)
                    }
                    analytics.logEvent(
                        "load_items",
                        "type" to "lucky_number_history",
                        "numbers" to it
                    )
                } else {
                    view?.run {
                        showContent(false)
                        showEmpty(true)
                        showErrorView(false)
                    }
                }
            }.onResourceNotLoading {
                view?.run {
                    showProgress(false)
                }
            }.launch()
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

    private fun reloadView(date: LocalDate) {
        currentDate = date
        Timber.i("Reload lucky number history view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            showContent(false)
            showEmpty(false)
            showErrorView(false)
            clearData()
            reloadNavigation()
        }
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

    private fun reloadNavigation() {
        view?.apply {
            showPreButton(!currentDate.minusDays(7).isHolidays)
            showNextButton(!currentDate.plusDays(7).isHolidays)
            updateNavigationWeek(
                "${currentDate.monday.toFormattedString("dd.MM")} - " +
                        currentDate.sunday.toFormattedString("dd.MM")
            )
        }
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        reloadView(LocalDate.of(year, month, day))
        loadData()
    }

    fun onPickDate() {
        view?.showDatePickerDialog(currentDate)
    }

    fun onPreviousWeek() {
        reloadView(currentDate.minusDays(7))
        loadData()
    }

    fun onNextWeek() {
        reloadView(currentDate.plusDays(7))
        loadData()
    }
}
