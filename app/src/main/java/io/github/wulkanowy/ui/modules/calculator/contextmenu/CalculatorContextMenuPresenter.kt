package io.github.wulkanowy.ui.modules.calculator.contextmenu

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class CalculatorContextMenuPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<CalculatorContextMenuView>(errorHandler, studentRepository) {

    fun onEditItemClicked() {
        view?.showEditDialog()
        view?.closeDialog()
    }

    fun onDeleteItemClicked() {
        view?.deleteItem()
        view?.closeDialog()
    }
}
