package io.github.wulkanowy.ui.modules.notificationscentre

import io.github.wulkanowy.data.repositories.NotificationRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class NotificationsCentrePresenter @Inject constructor(
    private val notificationRepository: NotificationRepository,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<NotificationsCentreView>(errorHandler, studentRepository) {

    override fun onAttachView(view: NotificationsCentreView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Notifications centre view was initialized")

        loadData()
    }

    private fun loadData() {
        notificationRepository.getNotifications()
            .map { notificationList -> notificationList.sortedByDescending { it.date } }
            .onEach {
                if (it.isEmpty()) {
                    view?.run {
                        showContent(false)
                        showProgress(false)
                        showEmpty(true)
                    }
                } else {
                    view?.run {
                        showContent(true)
                        showProgress(false)
                        showEmpty(false)
                        updateData(it)
                    }
                }
            }
            .launch()
    }
}