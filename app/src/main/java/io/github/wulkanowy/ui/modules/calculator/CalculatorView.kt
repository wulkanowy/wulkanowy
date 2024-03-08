package io.github.wulkanowy.ui.modules.calculator

import io.github.wulkanowy.ui.base.BaseView

interface CalculatorView : BaseView {
    fun initView()

    fun updateData(calculatorItemList: CalculatorItemList)

    fun showItemDialog(calculatorItem: CalculatorItem)

    fun showAddDialog()

    fun showEmpty()

    fun hideEmpty()

    fun showList()

    fun hideList()

    fun showOptionsDialog()

    fun clearCalculator()
}
