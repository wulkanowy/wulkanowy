package io.github.wulkanowy.ui.modules.login.advanced

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginAdvancedView : BaseView {

    val formNameValue: String
    val formPassValue: String
    val formHostValue: String?
    val formApiValue: String
    val formLoginType: String
    val formPinValue: String
    val formSymbolValue: String
    val formTokenValue: String

    fun initView()
    fun setDefaultCredentials(name: String, pass: String)
    fun setErrorNameRequired()
    fun setErrorPassRequired(focus: Boolean)
    fun setErrorPassInvalid(focus: Boolean)
    fun setErrorPassIncorrect()
    fun clearNameError()
    fun clearPassError()
    fun showSoftKeyboard()
    fun hideSoftKeyboard()
    fun showProgress(show: Boolean)
    fun showContent(show: Boolean)
    fun notifyParentAccountLogged(students: List<Student>)
    fun setErrorApiKeyInvalid()
    fun setErrorApiKeyRequired()
    fun setErrorPinRequired()
    fun setErrorSymbolRequired()
    fun setErrorTokenRequired()
    fun showOnlyHybridModeInputs()
    fun showOnlyScrapperModeInputs()
    fun showOnlyMobileApiModeInputs()
}
