package io.github.wulkanowy.utils

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass

class WidgetUpdater @Inject constructor(@ApplicationContext private val context: Context) {
    fun updateAllWidgets(widgetProvider: KClass<out BroadcastReceiver>) {
        try {
            val intent = Intent(context, widgetProvider::class.java)

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)

            val ids: IntArray = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, widgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

            context.sendBroadcast(intent)
        } catch (e: Exception) {
            Timber.w("Failed to update all widgets for provider $widgetProvider: $e")
        }
    }
}
