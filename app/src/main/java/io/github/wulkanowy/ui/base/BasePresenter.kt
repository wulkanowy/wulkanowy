package io.github.wulkanowy.ui.base

import io.reactivex.disposables.CompositeDisposable

open class BasePresenter<T : BaseView>(private val disposable: CompositeDisposable) {

    var view: T? = null

    val isViewAttached: Boolean
        get() = view != null

    open fun attachView(view: T) {
        this.view = view
    }

    open fun detachView() {
        view = null
        disposable.dispose()
    }
}
