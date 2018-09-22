package io.github.wulkanowy.ui.main

import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    fun initView()

    fun switchMenuFragment(position: Int)

    fun setViewTitle(title: String)

    fun viewTitles(): List<String>

    fun expandActionBar(show: Boolean)
}
