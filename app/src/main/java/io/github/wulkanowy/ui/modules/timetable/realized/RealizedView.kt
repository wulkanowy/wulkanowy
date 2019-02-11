package io.github.wulkanowy.ui.modules.timetable.realized

import io.github.wulkanowy.data.db.entities.Realized
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface RealizedView : BaseSessionView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<RealizedItem>)

    fun clearData()

    fun updateNavigationDay(date: String)

    fun hideRefresh()

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showRealizedDialog(realized: Realized)
}
