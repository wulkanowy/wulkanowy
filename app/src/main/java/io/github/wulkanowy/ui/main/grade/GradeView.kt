package io.github.wulkanowy.ui.main.grade

import io.github.wulkanowy.ui.base.BaseView

interface GradeView : BaseView {

    fun initView()

    fun loadChildData(semesterId: String, forceRefresh: Boolean, index: Int)

    fun notifyChildParentReselected(index: Int)

    fun showChildProgress(index: Int, showProgress: Boolean)

    fun currentPageIndex(): Int

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun showSemesterDialog(selectedIndex: Int)

    interface GradeChildView {

        fun loadData(semesterId: String, forceRefresh: Boolean)

        fun showChildProgress(showProgress: Boolean)

        fun onParentReselected()
    }
}
