package io.github.wulkanowy.ui.modules.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.repositories.mobiledevice.MobileDeviceRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class MobileDevicePresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val mobileDeviceRepository: MobileDeviceRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<MobileDeviceView>(errorHandler) {

    override fun onAttachView(view: MobileDeviceView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Mobile device view was initialized")
        loadData()
    }

    fun onSwipeRefresh() {
        loadData(true)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading devices data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { mobileDeviceRepository.getDevices(it, forceRefresh) }
            .map { items -> items.map { MobileDeviceItem(it) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    hideRefresh()
                    showProgress(false)
                    enableSwipe(true)
                }
            }.subscribe({
                view?.run {
                    updateData(it)
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                }
                analytics.logEvent("load_devices", "items" to it.size, "force_refresh" to forceRefresh)
            }) {
                Timber.i("Loading mobile devices result: An exception occurred")
                view?.run { showEmpty(isViewEmpty) }
                errorHandler.dispatch(it)
            })
    }

    fun unregisterDevice(device: MobileDevice) {
        Timber.i("Mobile device unregister started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { semester ->
                mobileDeviceRepository.unregister(semester, device)
                .flatMap { mobileDeviceRepository.getDevices(semester, it) }
            }
            .map { items -> items.map { MobileDeviceItem(it) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    showProgress(false)
                    enableSwipe(true)
                }
            }
            .subscribe({
                view?.run {
                    updateData(it)
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                }
            }) {
                errorHandler.dispatch(it)
            }
        )
    }

    fun registerDevice() {
        Timber.i("Mobile device registartion started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { mobileDeviceRepository.register(it) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                view?.showTokenDialog(it)
                analytics.logEvent("device_register", "symbol" to it.second.substring(0, 3))
            }) {
                errorHandler.dispatch(it)
            }
        )
    }
}
