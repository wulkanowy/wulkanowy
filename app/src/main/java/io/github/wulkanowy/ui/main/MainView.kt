package io.github.wulkanowy.ui.main

import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    fun initView()

    fun switchMenuView(position: Int)

    fun setViewTitle(title: String)

    fun setMenuViewReselected()

    fun expandActionBar(show: Boolean)

    fun viewTitle(index: Int): String

    fun currentMenuIndex(): Int

    interface MenuFragmentView {

        fun onFragmentReselected()
    }
}
