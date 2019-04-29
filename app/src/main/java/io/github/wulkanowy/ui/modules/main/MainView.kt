package io.github.wulkanowy.ui.modules.main

import androidx.fragment.app.Fragment
import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    var startMenuIndex: Int

    var startMenuFragment: Fragment?

    val isRootView: Boolean

    val currentViewTitle: String?

    val currentStackSize: Int?

    fun initView()

    fun switchMenuView(position: Int)

    fun showHomeArrow(show: Boolean)

    fun showAccountPicker()

    fun notifyMenuViewReselected()

    fun setViewTitle(title: String)

    fun popView()

    fun openLoginView()

    interface MainChildView {

        fun onFragmentReselected()
    }

    interface TitledView {

        val titleStringId: Int
    }
}
