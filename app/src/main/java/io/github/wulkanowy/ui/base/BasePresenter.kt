package io.github.wulkanowy.ui.base

import io.reactivex.disposables.CompositeDisposable

open class BasePresenter<T : BaseContract.View>(private val disposable: CompositeDisposable)
    : BaseContract.Presenter<T> {

    var view: T? = null

    val isViewAttached: Boolean
        get() = view != null

    override fun attachView(view: T) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.dispose()
    }
}
