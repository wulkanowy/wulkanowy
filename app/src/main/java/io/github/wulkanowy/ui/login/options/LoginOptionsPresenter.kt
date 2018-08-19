package io.github.wulkanowy.ui.login.options

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.BaseView
import javax.inject.Inject

class LoginOptionsPresenter @Inject constructor(errorHandler: ErrorHandler)
    : BasePresenter<BaseView>(errorHandler) {
}