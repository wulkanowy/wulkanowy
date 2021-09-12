package io.github.wulkanowy.ui.modules.settings.notifications

import io.github.wulkanowy.ui.base.BaseView

interface NotificationsView : BaseView {

    val isNotificationPermissionGranted: Boolean

    fun initView(showDebugNotificationSwitch: Boolean)

    fun showFixSyncDialog()

    fun openSystemSettings()

    fun enableNotification(notificationKey: String, enable: Boolean)

    fun openNotificationPermissionSettings()

    fun setNotificationPiggybackPreferenceChecked(isChecked: Boolean)
}
