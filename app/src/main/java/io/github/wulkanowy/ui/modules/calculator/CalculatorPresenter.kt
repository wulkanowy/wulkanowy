package io.github.wulkanowy.ui.modules.calculator

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class CalculatorPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    @ApplicationContext val context: Context,
    val calculator: Calculator
) : BasePresenter<CalculatorView>(errorHandler, studentRepository) {

    override fun onAttachView(view: CalculatorView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Calculator initialized.")
        errorHandler.showErrorMessage = ::showErrorViewOnError
    }

    fun showEmpty(isEmpty: Boolean) {
        if (isEmpty) view?.showEmpty() else view?.hideEmpty()
    }

    fun onViewReselected() {
    }

    fun updateData(items: Iterable<CalculatorItem>) {
        view?.run {
            val isEmpty = items.count() == 0
            showEmpty(isEmpty)
            showContent(isEmpty)
        }
    }

    fun loadData(items: Iterable<CalculatorItem>) {
        view?.run {
            val isEmpty = items.count() == 0
            showEmpty(isEmpty)
            showContent(isEmpty)
        }
    }

    private fun showContent(isEmpty: Boolean) {
        if (isEmpty) view?.hideList() else view?.showList()
    }

    private fun showErrorViewOnError(s: String, throwable: Throwable) {
        view?.showError(s, throwable)
    }

    fun onAddButtonClicked() {
        view?.showAddDialog()
    }

    fun onOptionsSelected(): Boolean {
        view?.showOptionsDialog()
        return true
    }

    fun onClearSelected(): Boolean {
        view?.clearCalculator()
        return true
    }
}
