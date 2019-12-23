package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    var startMenuIndex: Int

    val isRootView: Boolean

    val isDrawerOpened: Boolean

    val currentViewTitle: String?

    fun initView()

    fun switchMenuView(position: Int)

    fun showAccountPicker()

    fun showActionBarElevation(show: Boolean)

    fun setViewTitle(title: String)

    fun closeDrawer()

    fun popView(depth: Int = 1)

    interface TitledView {

        val titleStringId: Int
    }

    enum class Section(val id: Int) {
        GRADE(0),
        ATTENDANCE(1),
        EXAM(2),
        TIMETABLE(3),
        MESSAGE(5),
        HOMEWORK(6),
        NOTE(7),
        LUCKY_NUMBER(8),
        SETTINGS(9),
        ABOUT(10),
        SCHOOL(11)
    }
}
