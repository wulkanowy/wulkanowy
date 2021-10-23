package io.github.wulkanowy.ui.base

import androidx.annotation.StringRes

interface BaseView {

    fun showError(text: String, error: Throwable)

    fun showMessage(@StringRes text: Int)

    fun showMessage(text: String)

    fun showExpiredDialog()

    fun openClearLoginView()

    fun showErrorDetailsDialog(error: Throwable)

    fun showChangePasswordSnackbar(redirectUrl: String)
}
