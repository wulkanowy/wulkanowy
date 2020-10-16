package io.github.wulkanowy.ui.modules.account.accountdetails

import io.github.wulkanowy.ui.base.BaseView

interface AccountDetailsView : BaseView {

    fun showDefaultAvatar(name: String)
}