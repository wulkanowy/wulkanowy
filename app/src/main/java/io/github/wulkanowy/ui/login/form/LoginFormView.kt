package io.github.wulkanowy.ui.login.form

import io.github.wulkanowy.ui.base.BaseView

interface LoginFormView : BaseView {

    fun setErrorEmailRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorEmailInvalid()

    fun setErrorPassInvalid(focus: Boolean)

    fun setErrorPassIncorrect()

    fun resetViewErrors()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showActionBar(show: Boolean)

    fun showLoginProgress(show: Boolean)
}