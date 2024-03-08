package io.github.wulkanowy.ui.modules.grade.details.contextmenu

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class GradeHeaderContextMenuPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<GradeHeaderContextMenu>(errorHandler, studentRepository) {

    fun newCalculator() {
        view?.clearCalculator()
        view?.appendCalculator()
        view?.closeDialog()
        view?.openCalculator()
    }

    fun appendCalculator() {
        view?.appendCalculator()
        view?.closeDialog()
        view?.openCalculator()
    }
}
