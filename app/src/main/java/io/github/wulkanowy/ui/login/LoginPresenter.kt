package io.github.wulkanowy.ui.login

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class LoginPresenter @Inject constructor(errorHandler: ErrorHandler)
    : BasePresenter<LoginView>(errorHandler) {

}
