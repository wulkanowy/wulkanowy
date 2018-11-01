package io.github.wulkanowy.ui.widgets.timetable

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getBroadcast
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.android.AndroidInjection
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.services.widgets.TimetableWidgetService
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.previousOrSameSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.weekDayName
import org.threeten.bp.LocalDate
import javax.inject.Inject

class TimetableWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var sharedPref: SharedPrefHelper

    private var currentDate = LocalDate.now().nextOrSameSchoolDay

    companion object {
        const val EXTRA_SELECTED_DATE = "extraSelectedDate"

        const val ACTION_TOGGLE_NEXT = "actionToggleNext"

        const val ACTION_TOGGLE_PREV = "actionTogglePrev"
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach {

            if (sharedPref.getLong("timetable_widget", -1) == -1L || sharedPref.getLong("timetable_widget", -1) != currentDate.toEpochDay()) {
                sharedPref.putLong("timetable_widget", currentDate.toEpochDay())
            }

            currentDate = LocalDate.ofEpochDay(sharedPref.getLong("timetable_widget", -1))

            context?.run {
                RemoteViews(packageName, R.layout.widget_timetable).apply {
                    setTextViewText(R.id.timetableWidgetDay, currentDate.weekDayName)
                    setTextViewText(R.id.timetableWidgetDate, currentDate.toFormattedString())
                    setEmptyView(R.id.timetableWidgetList, R.id.timetableWidgetEmpty)
                    setRemoteAdapter(R.id.timetableWidgetList, Intent(context, TimetableWidgetService::class.java)
                        .apply { putExtra(EXTRA_SELECTED_DATE, currentDate.toEpochDay()) })

                    setOnClickPendingIntent(R.id.timetableWidgetNext,
                        PendingIntent.getBroadcast(context, 1,
                            Intent(context, TimetableWidgetProvider::class.java).apply {
                                action = ACTION_APPWIDGET_UPDATE
                                putExtra("toggle", ACTION_TOGGLE_NEXT)
                                putExtra(EXTRA_APPWIDGET_IDS, appWidgetIds)
                            },
                            FLAG_UPDATE_CURRENT))

                    setOnClickPendingIntent(R.id.timetableWidgetPrev,
                        getBroadcast(context, 2,
                            Intent(context, TimetableWidgetProvider::class.java).apply {
                                action = ACTION_APPWIDGET_UPDATE
                                putExtra("toggle", ACTION_TOGGLE_PREV)
                                putExtra(EXTRA_APPWIDGET_IDS, appWidgetIds)
                            },
                            FLAG_UPDATE_CURRENT))

                }.let { view ->
                    appWidgetManager?.notifyAppWidgetViewDataChanged(it, R.id.timetableWidgetList)
                    appWidgetManager?.updateAppWidget(it, view)
                }
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)

        intent?.let {
            when (it.getStringExtra("toggle")) {
                ACTION_TOGGLE_NEXT -> currentDate = LocalDate.ofEpochDay(sharedPref.getLong("timetable_widget", -1))
                    .plusDays(1).nextOrSameSchoolDay
                ACTION_TOGGLE_PREV -> currentDate = LocalDate.ofEpochDay(sharedPref.getLong("timetable_widget", -1))
                    .minusDays(1).previousOrSameSchoolDay
            }
        }
        super.onReceive(context, intent)
    }
}
