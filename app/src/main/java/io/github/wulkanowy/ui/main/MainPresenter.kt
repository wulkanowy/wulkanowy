package io.github.wulkanowy.ui.main

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class MainPresenter @Inject constructor(
        errorHandler: ErrorHandler,
        private val prefRepository: PreferencesRepository)
    : BasePresenter<MainView>(errorHandler) {

    override fun onAttachView(view: MainView) {
        super.onAttachView(view)
        view.run {
            startMenuIndex = prefRepository.startMenuIndex
            initView()
        }
    }

    fun onStartView() {
        view?.run { setViewTitle(getViewTitle(currentMenuIndex)) }
    }

    fun onMenuViewChange(index: Int) {
        view?.run { setViewTitle(getViewTitle(index)) }
    }

    fun onTabSelected(index: Int, wasSelected: Boolean): Boolean {
        return view?.run {
            if (wasSelected) {
                notifyMenuViewReselected()
                false
            } else {
                switchMenuView(index)
                true
            }
        } == true
    }
}
