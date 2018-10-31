package io.github.wulkanowy.ui.widgets.timetable

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.android.AndroidInjection
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.services.widgets.TimetableWidgetService
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.weekDayName
import org.threeten.bp.LocalDate
import javax.inject.Inject

class TimetableWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var sharedPref: SharedPrefHelper

    private val currentDate = LocalDate.now().nextOrSameSchoolDay

    companion object {
        const val EXTRA_SELECTED_DATE = "extraSelectedDate"

        const val ACTION_TOGGLE_NEXT = "actionToggleNext"

        const val ACTION_TOGGLE_PRE = "actionTogglePrev"
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        AndroidInjection.inject(this, context)
        appWidgetIds?.forEach {
            context?.run {
                RemoteViews(packageName, R.layout.widget_timetable).apply {
                    setTextViewText(R.id.timetableWidgetDay, currentDate.weekDayName)
                    setTextViewText(R.id.timetableWidgetDate, currentDate.toFormattedString())
                    setEmptyView(R.id.timetableWidgetList, R.id.timetableWidgetEmpty)
                    setRemoteAdapter(R.id.timetableWidgetList, Intent(context, TimetableWidgetService::class.java)
                            .apply { putExtra(EXTRA_SELECTED_DATE, currentDate.toEpochDay()) })
                }.let { view -> appWidgetManager?.updateAppWidget(it, view) }
            }
        }
    }
}
