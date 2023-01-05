package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class MenuOrderPresenter @Inject constructor(
    studentRepository: StudentRepository,
    errorHandler: ErrorHandler,
    private val preferencesRepository: PreferencesRepository
) : BasePresenter<MenuOrderView>(errorHandler, studentRepository) {

    private var updatedAppMenuItems = emptyList<AppMenuItem>()

    override fun onAttachView(view: MenuOrderView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Menu order view was initialized")
        loadData()
    }

    private fun loadData() {
        val savedMenuItemList = (preferencesRepository.appMenuItemOrder)
            .sortedBy { it.order }

        view?.updateData(savedMenuItemList)
    }

    fun onDragAndDropEnd(list: List<AppMenuItem>) {
        val updatedList = list.mapIndexed { index, menuItem -> menuItem.apply { order = index } }

        updatedAppMenuItems = updatedList
        view?.updateData(updatedList)
    }

    fun onBackSelected() {
        if (updatedAppMenuItems.isNotEmpty()) {
            view?.showRestartConfirmationDialog()
        } else {
            view?.popView()
        }
    }

    fun onConfirmRestart() {
        preferencesRepository.appMenuItemOrder = updatedAppMenuItems
        view?.restartApp()
    }

    fun onCancelRestart() {
        view?.popView()
    }
}
