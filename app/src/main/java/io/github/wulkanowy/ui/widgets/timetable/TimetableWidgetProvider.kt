package io.github.wulkanowy.ui.widgets.timetable

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.wulkanowy.R
import io.github.wulkanowy.services.widgets.TimetableWidgetService

class TimetableWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach {
            context?.run {
                RemoteViews(packageName, R.layout.widget_timetable).apply {
                    setEmptyView(R.id.timetableWidgetList, R.id.timetableWidgetEmpty)
                    setRemoteAdapter(R.id.timetableWidgetList, Intent(context, TimetableWidgetService::class.java)
                            .apply { putExtra(EXTRA_APPWIDGET_ID, it) })
                }.let { view -> appWidgetManager?.updateAppWidget(it, view) }
            }
        }
    }
}
