package io.github.wulkanowy.ui.main

import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    var startMenuIndex: Int

    val currentViewTitle: String?

    fun initView()

    fun switchMenuView(position: Int)

    fun notifyMenuViewReselected()

    fun setViewTitle(title: String)

    interface MainChildView {

        fun onFragmentReselected()
    }

    interface TitledView {

        val titleStringId: Int
    }
}
