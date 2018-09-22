package io.github.wulkanowy.ui.main

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class MainPresenter @Inject constructor(errorHandler: ErrorHandler)
    : BasePresenter<MainView>(errorHandler) {

    override fun attachView(view: MainView) {
        super.attachView(view)
        view.initView()
    }

    fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
        return if (!wasSelected) {
            view?.switchMenuFragment(position)
            view?.expandActionBar(true)
            true
        } else false
    }

    fun onMenuFragmentChange(position: Int) {
        view?.run {
            setViewTitle(viewTitles()[position])
        }
    }
}

