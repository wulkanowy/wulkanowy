package io.github.wulkanowy.ui.main

import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    fun initFragmentController()

    fun initBottomNav()
}