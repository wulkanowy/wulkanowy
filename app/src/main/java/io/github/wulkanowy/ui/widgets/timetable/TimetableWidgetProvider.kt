package io.github.wulkanowy.ui.widgets.timetable

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
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
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainActivity.Companion.EXTRA_START_MENU_INDEX
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.previousOrSameSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.weekDayName
import org.threeten.bp.LocalDate
import javax.inject.Inject

class TimetableWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var sharedPref: SharedPrefHelper

    companion object {
        const val EXTRA_TOGGLED_WIDGET_ID = "extraToggledWidget"

        const val EXTRA_TOGGLE_VALUE = "extraToggleValue"

        const val TOGGLE_NEXT = "actionToggleNext"

        const val TOGGLE_PREV = "actionTogglePrev"
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach {
            val widgetKey = "timetable_widget_$it"
            checkSavedWidgetDate(widgetKey)

            val savedDate = LocalDate.ofEpochDay(sharedPref.getLong(widgetKey, 0))
            context?.run {
                RemoteViews(packageName, R.layout.widget_timetable).apply {
                    setTextViewText(R.id.timetableWidgetDay, savedDate.weekDayName.capitalize())
                    setTextViewText(R.id.timetableWidgetDate, savedDate.toFormattedString())
                    setEmptyView(R.id.timetableWidgetList, R.id.timetableWidgetEmpty)
                    setRemoteAdapter(R.id.timetableWidgetList, Intent(context, TimetableWidgetService::class.java)
                        .apply { action = widgetKey })

                    setOnClickPendingIntent(R.id.timetableWidgetNext,
                        PendingIntent.getBroadcast(context, it,
                            Intent(context, TimetableWidgetProvider::class.java).apply {
                                action = ACTION_APPWIDGET_UPDATE
                                putExtra(EXTRA_APPWIDGET_IDS, appWidgetIds)
                                putExtra(EXTRA_TOGGLE_VALUE, TOGGLE_NEXT)
                                putExtra(EXTRA_TOGGLED_WIDGET_ID, it)
                            }, FLAG_UPDATE_CURRENT))

                    setOnClickPendingIntent(R.id.timetableWidgetPrev,
                        PendingIntent.getBroadcast(context, -it,
                            Intent(context, TimetableWidgetProvider::class.java).apply {
                                action = ACTION_APPWIDGET_UPDATE
                                putExtra(EXTRA_APPWIDGET_IDS, appWidgetIds)
                                putExtra(EXTRA_TOGGLE_VALUE, TOGGLE_PREV)
                                putExtra(EXTRA_TOGGLED_WIDGET_ID, it)
                            }, FLAG_UPDATE_CURRENT))

                    setPendingIntentTemplate(R.id.timetableWidgetList,
                        PendingIntent.getActivity(context, 1, MainActivity.getStartIntent(context).apply {
                            putExtra(EXTRA_START_MENU_INDEX, 3)
                        }, FLAG_UPDATE_CURRENT))

                }.also { view ->
                    appWidgetManager?.notifyAppWidgetViewDataChanged(it, R.id.timetableWidgetList)
                    appWidgetManager?.updateAppWidget(it, view)
                }
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)
        intent?.let {
            val widgetKey = "timetable_widget_${it.getIntExtra(EXTRA_TOGGLED_WIDGET_ID, 0)}"
            when (it.getStringExtra(EXTRA_TOGGLE_VALUE)) {
                TOGGLE_NEXT -> {
                    LocalDate.ofEpochDay(sharedPref.getLong(widgetKey, 0)).plusDays(1).nextOrSameSchoolDay
                        .let { date -> sharedPref.putLong(widgetKey, date.toEpochDay(), true) }
                }
                TOGGLE_PREV -> {
                    LocalDate.ofEpochDay(sharedPref.getLong(widgetKey, 0)).minusDays(1).previousOrSameSchoolDay
                        .let { date -> sharedPref.putLong(widgetKey, date.toEpochDay(), true) }
                }
            }
        }
        super.onReceive(context, intent)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach {
            sharedPref.delete("timetable_widget_$it")
        }
    }

    private fun checkSavedWidgetDate(widgetKey: String) {
        sharedPref.run {
            if (getLong(widgetKey, -1) == -1L) {
                putLong(widgetKey, LocalDate.now().nextOrSameSchoolDay.toEpochDay(), true)
            }
        }
    }
}
