package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.ui.base.BaseView

interface AccountView : BaseView {

    fun initView()

    fun updateData(data: List<AccountItem>, footer: AccountScrollableFooter)

    fun dismissView()

    fun openLoginView()

    fun recreateView()
}

