package io.github.wulkanowy.ui.modules.grade.details.contextmenu

import io.github.wulkanowy.ui.base.contextmenu.ContextMenuItem

interface GradeHeaderContextMenuView {
    fun initView()
    fun closeDialog()
    fun appendCalculator()
    val newCalculatorItem: ContextMenuItem
    val appendToCalculatorItem: ContextMenuItem
    fun clearCalculator()
    fun openCalculator()
}
