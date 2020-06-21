package io.github.wulkanowy.ui.modules.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.repositories.mobiledevice.MobileDeviceRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@ExperimentalCoroutinesApi
class MobileDevicePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val mobileDeviceRepository: MobileDeviceRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<MobileDeviceView>(errorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    private var refreshJob: Job? = null

    private var loadingJob: Job? = null

    override fun onAttachView(view: MobileDeviceView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Mobile device view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
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

    private fun refreshData() {
        refreshJob?.cancel()
        refreshJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emit(mobileDeviceRepository.refreshDevices(student, semester))
            }.onCompletion { afterLoading() }.catch { handleError(it) }.collect()
        }
    }

    private fun loadData() {
        Timber.i("Loading mobile devices data started")

        loadingJob?.cancel()
        loadingJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emitAll(mobileDeviceRepository.getDevices(student, semester))
            }.onEach {
                afterLoading()
            }.catch {
                handleError(it)
            }.collect {
                Timber.i("Loading mobile devices result: Success")
                view?.run {
                    updateData(it)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    showErrorView(false)
                }
                analytics.logEvent(
                    "load_data",
                    "type" to "devices",
                    "items" to it.size
                )
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
        Timber.i("Loading mobile devices result: An exception occurred")
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

    fun onRegisterDevice() {
        view?.showTokenDialog()
    }

    fun onUnregisterDevice(device: MobileDevice, position: Int) {
        view?.run {
            deleteItem(device, position)
            showUndo(device, position)
            showEmpty(isViewEmpty)
        }
    }

    fun onUnregisterCancelled(device: MobileDevice, position: Int) {
        view?.run {
            restoreDeleteItem(device, position)
            showEmpty(isViewEmpty)
        }
    }

    fun onUnregisterConfirmed(device: MobileDevice) {
        Timber.i("Unregister device started")
        launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                mobileDeviceRepository.unregisterDevice(student, semester, device)
                emit(mobileDeviceRepository.refreshDevices(student, semester))
            }.catch {
                Timber.i("Unregister device result: An exception occurred")
                errorHandler.dispatch(it)
            }.onCompletion {
                view?.run {
                    showProgress(false)
                    enableSwipe(true)
                }
            }.collect {
                Timber.i("Unregister device result: Success")
            }
        }
    }
}
