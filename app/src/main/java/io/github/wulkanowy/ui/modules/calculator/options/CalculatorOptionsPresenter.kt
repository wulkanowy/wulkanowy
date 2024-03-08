package io.github.wulkanowy.ui.modules.calculator.options

import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class CalculatorOptionsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    val preferencesRepository: PreferencesRepository
) : BasePresenter<CalculatorOptionsView>(errorHandler, studentRepository) {

    fun onSaveButtonClicked() {
        val minusValue = view?.readMinusInput() ?: run {
            view?.showMinusInputError()
            return@onSaveButtonClicked
        }

        val plusValue = view?.readPlusInput() ?: run {
            view?.showPlusInputError()
            return@onSaveButtonClicked
        }

        preferencesRepository.calculatorPlusValue = plusValue
        preferencesRepository.calculatorMinusValue = minusValue

        view?.resetCalculator()
        view?.closeDialog()
    }

    fun onCloseButtonClicked() {
        view?.closeDialog()
    }

    override fun onAttachView(view: CalculatorOptionsView) {
        super.onAttachView(view)
        view.initView()
    }
}
