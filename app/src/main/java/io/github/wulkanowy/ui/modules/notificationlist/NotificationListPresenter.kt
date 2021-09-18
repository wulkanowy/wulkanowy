package io.github.wulkanowy.ui.modules.notificationlist

import io.github.wulkanowy.data.repositories.NotificationRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class NotificationListPresenter @Inject constructor(
    private val notificationRepository: NotificationRepository,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<NotificationListView>(errorHandler, studentRepository) {
}