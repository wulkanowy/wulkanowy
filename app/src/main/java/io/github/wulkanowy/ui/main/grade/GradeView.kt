package io.github.wulkanowy.ui.main.grade

import io.github.wulkanowy.ui.base.BaseView

interface GradeView : BaseView {

    fun initView()

    fun loadChildViewData(semesterId: String, forceRefresh: Boolean, index: Int)

    fun currentPageIndex(): Int

    fun showChildProgress()

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun showSemesterDialog(selectedIndex: Int)
}
