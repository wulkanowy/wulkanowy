package io.github.wulkanowy.ui.modules.attendance.calculator

import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.ui.base.BaseView

interface AttendanceCalculatorView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun showRefresh(show: Boolean)

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun updateData(data: List<Pair<String, AttendanceSummary>>)

    fun setTargetAttendance(targetFreq: Double)

    fun clearView()
}
