package io.github.wulkanowy.ui.login.options

import io.github.wulkanowy.ui.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginOptionsPresenter @Inject constructor(disposable: CompositeDisposable)
    : BasePresenter<LoginOptionsContract.View>(disposable), LoginOptionsContract.Presenter {
}