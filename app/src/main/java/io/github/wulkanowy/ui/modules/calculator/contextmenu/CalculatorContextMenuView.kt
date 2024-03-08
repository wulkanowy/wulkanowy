package io.github.wulkanowy.ui.modules.calculator.contextmenu

import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.base.contextmenu.ContextMenuItem

interface CalculatorContextMenuView : BaseView {
    val deleteItem: ContextMenuItem
    val editItem: ContextMenuItem
    fun showEditDialog()
    fun deleteItem()
    fun closeDialog()
}
