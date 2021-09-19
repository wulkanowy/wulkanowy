package io.github.wulkanowy.ui.modules.notificationscentre

import io.github.wulkanowy.data.repositories.NotificationRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
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
    }

    private fun loadData() {
    }
}