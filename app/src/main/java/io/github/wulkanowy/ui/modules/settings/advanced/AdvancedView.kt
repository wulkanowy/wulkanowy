package io.github.wulkanowy.ui.modules.settings.advanced

import io.github.wulkanowy.ui.base.BaseView

interface AdvancedView : BaseView {

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
