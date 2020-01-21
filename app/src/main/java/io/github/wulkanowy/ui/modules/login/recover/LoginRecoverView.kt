package io.github.wulkanowy.ui.modules.login.recover

import io.github.wulkanowy.ui.base.BaseView

interface LoginRecoverView : BaseView {

    val recoverHostValue: String?

    val recoverNameValue: String

    val recoverSymbolValue: String

    val recoverWebViewSuccess: Boolean

    fun initView()

    fun setDefaultCredentials(name: String, symbol: String)

    fun clearNameError()

    fun clearSymbolError()

    fun setErrorNameRequired()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showContentForm(show: Boolean)

    fun showContentCaptcha(show: Boolean)

    fun showError(show: Boolean)

    fun loadRecaptcha(siteKey: String, url: String)

}
