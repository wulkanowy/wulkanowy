package io.github.wulkanowy.ui.modules.debug

import io.github.wulkanowy.ui.base.BaseView

interface DebugView : BaseView {
    fun openLogViewer()
    fun initView()
    fun setItems(itemList: List<DebugItem>)
}
