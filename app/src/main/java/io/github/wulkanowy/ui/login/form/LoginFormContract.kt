package io.github.wulkanowy.ui.login.form

import io.github.wulkanowy.ui.base.BaseContract

interface LoginFormContract {

    interface View : BaseContract.View {
        fun setErrorEmailRequired()

        fun setErrorPassRequired(focus: Boolean)

        fun setErrorEmailInvalid()

        fun setErrorPassInvalid(focus: Boolean)

        fun setErrorPassIncorrect()

        fun resetViewErrors()

        fun showSoftKeyboard()

        fun hideSoftKeyboard()

        fun showActionBar(show: Boolean)
    }

    interface Presenter : BaseContract.Presenter<View> {

        fun attemptLogin(email: String, password: String)
    }
}