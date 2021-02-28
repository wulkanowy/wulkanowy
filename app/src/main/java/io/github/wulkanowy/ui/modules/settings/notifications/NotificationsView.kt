package io.github.wulkanowy.ui.modules.settings.notifications

import io.github.wulkanowy.ui.base.BaseView

interface NotificationsView : BaseView {

    val syncSuccessString: String

    val syncFailedString: String

    fun initView()

    fun recreateView()

    fun setServicesSuspended(serviceEnablesKey: String, isHolidays: Boolean)

    fun showFixSyncDialog()
}
