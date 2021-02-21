package io.github.wulkanowy.ui.modules.settings.notifications

import io.github.wulkanowy.ui.base.BaseView

interface NotificationsView : BaseView {

    val syncSuccessString: String

    val syncFailedString: String

    fun initView()

    fun recreateView()

    fun updateLanguage(langCode: String)

    fun updateLanguageToFollowSystem()

    fun setServicesSuspended(serviceEnablesKey: String, isHolidays: Boolean)

    fun setSyncInProgress(inProgress: Boolean)

    fun showFixSyncDialog()
}
