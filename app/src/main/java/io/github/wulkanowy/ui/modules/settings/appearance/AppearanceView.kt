package io.github.wulkanowy.ui.modules.settings.appearance

import io.github.wulkanowy.ui.base.BaseView

interface AppearanceView : BaseView {

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
