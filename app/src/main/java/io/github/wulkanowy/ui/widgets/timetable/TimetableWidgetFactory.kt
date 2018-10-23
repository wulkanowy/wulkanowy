package io.github.wulkanowy.ui.widgets.timetable

import android.widget.RemoteViewsService

class TimetableWidgetFactory : RemoteViewsService.RemoteViewsFactory {

    override fun onCreate() {}

    override fun getLoadingView() = null

    override fun getItemId(position: Int) = position.toLong()

    override fun onDataSetChanged() {
    }

    override fun hasStableIds() = true

    override fun getViewAt(position: Int) = null

    override fun getCount() = 0

    override fun getViewTypeCount() = 1

    override fun onDestroy() {
    }
}
