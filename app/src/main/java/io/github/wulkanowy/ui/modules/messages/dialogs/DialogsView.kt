package io.github.wulkanowy.ui.modules.messages.dialogs

import io.github.wulkanowy.ui.base.BaseView

interface DialogsView : BaseView {

    val isViewEmpty: Boolean

    fun updateData(dialogs: List<Dialog>)

    fun initView()

    fun showEmpty(show: Boolean)

    fun hideRefresh()

    fun showProgress(show: Boolean)
}
