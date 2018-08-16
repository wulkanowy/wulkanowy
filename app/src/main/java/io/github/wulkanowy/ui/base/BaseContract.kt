package io.github.wulkanowy.ui.base

interface BaseContract {

    interface View {

        fun showMessage(text: String)

        fun showNoNetworkMessage()
    }

    interface Presenter<T : BaseContract.View> {

        fun attachView(view: T)

        fun detachView()
    }
}
