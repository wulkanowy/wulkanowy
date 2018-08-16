package io.github.wulkanowy.ui.login

import io.github.wulkanowy.ui.base.BaseContract

interface LoginContract {
    interface View : BaseContract.View {

        fun setErrorEmailRequired()

        fun setErrorPassRequired()

        fun setErrorEmailInvalid()

        fun setErrorPassInvalid()

        fun setErrorPassIncorrect()

        fun resetViewErrors()

        fun setStepOneLoginProgress()

        fun setStepTwoLoginProgress()

        fun openMainActivity()

        fun showLoginProgress(show: Boolean)

        fun showSoftKeyboard()

        fun hideSoftKeyboard()

        fun showActionBar(show: Boolean)

        fun onSyncFailed()

    }

    interface Presenter : BaseContract.Presenter<View> {

        fun attemptLogin(email: String, password: String)
    }
}
