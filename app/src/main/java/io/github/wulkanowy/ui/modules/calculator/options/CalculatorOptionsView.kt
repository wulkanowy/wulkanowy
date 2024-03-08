package io.github.wulkanowy.ui.modules.calculator.options

import io.github.wulkanowy.ui.base.BaseView

interface CalculatorOptionsView : BaseView {
    fun initView()
    fun readMinusInput(): Double?
    fun readPlusInput(): Double?
    fun showMinusInputError()
    fun showPlusInputError()
    fun closeDialog()
    fun resetCalculator()
}
