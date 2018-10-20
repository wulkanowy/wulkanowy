package io.github.wulkanowy.ui.main

import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    var startMenuIndex: Int

    val currentMenuIndex: Int

    fun initView()

    fun switchMenuView(position: Int)

    fun notifyMenuViewReselected()

    fun setViewTitle(title: String)

    fun getViewTitle(index: Int): String

    interface MenuFragmentView {

        fun onFragmentReselected()
    }
}
