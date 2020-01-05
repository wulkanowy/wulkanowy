package io.github.wulkanowy.ui.modules.login.recover

import io.github.wulkanowy.ui.base.BaseView

interface LoginRecoverView : BaseView {

    val recoverHostValue: String?

    val recoverNameValue: String

    val recoverSymbolValue: String

    fun initView()

    fun setDefaultCredentials(name: String, symbol: String)

    fun clearNameError()

    fun clearSymbolError()

    fun setErrorNameRequired()

    fun showProgress(show: Boolean)

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showContentForm(show: Boolean)

    fun showContentCaptcha(show: Boolean)

    fun loadRecaptcha(siteKey: String, URL: String)

}