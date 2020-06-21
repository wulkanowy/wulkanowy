package io.github.wulkanowy.ui.modules.luckynumber

import io.github.wulkanowy.data.repositories.luckynumber.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LuckyNumberPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val luckyNumberRepository: LuckyNumberRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LuckyNumberView>(errorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    private var refreshJob: Job? = null

    private var loadingJob: Job? = null

    override fun onAttachView(view: LuckyNumberView) {
        super.onAttachView(view)
        view.run {
            initView()
            showContent(false)
            enableSwipe(false)
        }
        Timber.i("Lucky number view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    private fun refreshData() {
        refreshJob?.cancel()
        refreshJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                emit(luckyNumberRepository.refreshLuckyNumber(student))
            }.onCompletion { afterLoading() }.catch { handleError(it) }.collect()
        }
    }

    private fun loadData() {
        Timber.i("Loading lucky number started")

        loadingJob?.cancel()
        loadingJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                emitAll(luckyNumberRepository.getLuckyNumber(student))
            }.onEach {
                afterLoading()
            }.catch {
                handleError(it)
            }.collect {
                if (it != null) {
                    Timber.i("Loading lucky number result: Success")
                    view?.apply {
                        updateData(it)
                        showContent(true)
                        showEmpty(false)
                        showErrorView(false)
                    }
                    analytics.logEvent(
                        "load_item",
                        "type" to "lucky_number",
                        "number" to it.luckyNumber
                    )
                } else {
                    Timber.i("Loading lucky number result: No lucky number found")
                    view?.run {
                        showContent(false)
                        showEmpty(true)
                        showErrorView(false)
                    }
                }
            }
        }
    }

    private fun afterLoading() {
        view?.run {
            hideRefresh()
            showProgress(false)
            enableSwipe(true)
        }
    }

    private fun handleError(error: Throwable) {
        Timber.i("Loading lucky number result: An exception occurred")
        errorHandler.dispatch(error)
        afterLoading()
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

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the lucky number")
        refreshData()
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        refreshData()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }
}
