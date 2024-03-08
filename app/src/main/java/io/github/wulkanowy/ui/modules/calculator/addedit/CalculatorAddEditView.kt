package io.github.wulkanowy.ui.modules.calculator.addedit

import io.github.wulkanowy.ui.base.BaseView

interface CalculatorAddEditView : BaseView {
    fun closeDialog()
    fun initView()

    val titleText: String
    val gradeText: String
    val weightText: String

    fun updateItem(title: String?, grade: Double, weight: Double, originalGrade: String?)
    fun saveNewItem(title: String?, grade: Double, weight: Double, originalGrade: String?)

    fun showGradeError(gradeText: String)
    fun showWeightError(weightText: String)
}
