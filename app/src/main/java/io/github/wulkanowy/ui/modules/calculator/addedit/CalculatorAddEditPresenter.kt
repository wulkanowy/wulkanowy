package io.github.wulkanowy.ui.modules.calculator.addedit

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.calculator.Calculator
import javax.inject.Inject

class CalculatorAddEditPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    val calculator: Calculator
) : BasePresenter<CalculatorAddEditView>(errorHandler, studentRepository) {

    override fun onAttachView(view: CalculatorAddEditView) {
        super.onAttachView(view)
        view.initView()
    }

    private fun getGradeOrNull(): Pair<Double, String?>? {
        val gradeText = view?.gradeText ?: return null
        val grade = calculator.parseGrade(gradeText)
        return if (grade == null) {
            view?.showGradeError(gradeText)
            null
        } else grade to gradeText
    }

    private fun getWeightOrNull(): Double? {
        val weightText = view?.weightText ?: return null
        val weight = calculator.parseWeight(weightText)
        if (weight == null) {
            view?.showWeightError(weightText)
        }
        return weight
    }

    private fun getTitleOrNull(): String? = view?.titleText

    fun onAddItemClicked() {
        val grade = getGradeOrNull() ?: return
        val weight = getWeightOrNull() ?: return
        val title = getTitleOrNull()
        view?.saveNewItem(title, grade.first, weight, grade.second)
        view?.closeDialog()
    }

    fun onUpdateItemClicked() {
        val grade = getGradeOrNull() ?: return
        val weight = getWeightOrNull() ?: return
        val title = getTitleOrNull()
        view?.updateItem(title, grade.first, weight, grade.second)
        view?.closeDialog()
    }
}
