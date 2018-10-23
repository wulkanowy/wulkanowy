package io.github.wulkanowy.ui.widgets.timetable

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import dagger.android.AndroidInjection

class TimetableWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        AndroidInjection.inject(this, context)
    }
}
