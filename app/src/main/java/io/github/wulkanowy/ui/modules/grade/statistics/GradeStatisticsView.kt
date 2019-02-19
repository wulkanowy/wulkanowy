package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface GradeStatisticsView : BaseSessionView {

    fun initView()

    fun updateSubjects(data: ArrayList<String>)

    fun updateData(items: List<GradeStatistics>)

    fun showSubjects(show: Boolean)

    fun notifyParentDataLoaded(semesterId: Int)

    fun notifyParentRefresh()
}
