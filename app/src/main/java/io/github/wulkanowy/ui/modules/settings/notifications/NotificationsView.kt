package io.github.wulkanowy.ui.modules.settings.notifications

import io.github.wulkanowy.ui.base.BaseView

interface NotificationsView : BaseView {

    fun initView()

    fun showFixSyncDialog()

    fun enableNotification(notificationKey: String, enable: Boolean)
}
