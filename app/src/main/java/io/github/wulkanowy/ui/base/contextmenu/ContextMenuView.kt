package io.github.wulkanowy.ui.base.contextmenu

import io.github.wulkanowy.ui.base.BaseView

interface ContextMenuView : BaseView {
    fun initView()
    fun closeDialog()
}
