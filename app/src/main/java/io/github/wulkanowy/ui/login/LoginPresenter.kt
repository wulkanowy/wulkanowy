package io.github.wulkanowy.ui.login

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class LoginPresenter @Inject constructor(errorHandler: ErrorHandler)
    : BasePresenter<LoginView>(errorHandler) {

    override fun attachView(view: LoginView) {
        super.attachView(view)
        view.run {
            initAdapter()
            hideActionBar()
        }
    }

    fun onPageSelected(index: Int) {
        view?.loadOptionsView(index)
    }

    fun onSwitchFragment(position: Int) {
        view?.switchView(position)
    }

}
