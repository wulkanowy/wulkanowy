package io.github.wulkanowy.ui.modules.timetablewidget

import io.github.wulkanowy.ui.base.BaseView
import java.io.Serializable

interface TimetableWidgetConfigureView : BaseView {

    fun initView()

    fun updateData(data: List<TimetableWidgetConfigureItem>)

    fun updateTimetableWidget(widgetId: Int, student: Serializable)

    fun setSuccessResult(widgetId: Int)

    fun finishView()
}