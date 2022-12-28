package io.github.wulkanowy.ui.modules.login.symbol

import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.login.LoginData

interface LoginSymbolView : BaseView {

    val symbolNameError: CharSequence?

    fun initView()

    fun setLoginToHeading(login: String)

    fun setErrorSymbolIncorrect()

    fun setErrorSymbolRequire()

    fun clearSymbolError()

    fun clearAndFocusSymbol()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun navigateToStudentSelect(loginData: LoginData, registerUser: RegisterUser)

    fun showContact(show: Boolean)

    fun openFaqPage()

    fun openEmail(host: String, lastError: String)
}
