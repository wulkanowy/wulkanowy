package io.github.wulkanowy.ui.modules.luckynumberwidget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.services.sync.works.LuckyNumberWork
import io.github.wulkanowy.ui.base.BaseAppWidgetProvider
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getRefreshKey
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LuckyNumberWidgetProvider : BaseAppWidgetProvider() {

    @Inject
    lateinit var luckyNumberRepository: LuckyNumberRepository

    @Inject
    lateinit var syncManager: SyncManager

    companion object {
        const val UPDATE_REFRESH_KEY = "update_refresh_key"
        const val UPDATE_REFRESH_ERROR_KEY = "update_refresh_error_key"
        fun getStudentWidgetKey(appWidgetId: Int) = "lucky_number_widget_student_$appWidgetId"
        fun getThemeWidgetKey(appWidgetId: Int) = "lucky_number_widget_theme_$appWidgetId"
        fun getHeightWidgetKey(appWidgetId: Int) = "lucky_number_widget_height_$appWidgetId"
        fun getWidthWidgetKey(appWidgetId: Int) = "lucky_number_widget_width_$appWidgetId"
    }

    override fun onUpdate(context: Context, appWidgetIds: IntArray, extras: Bundle?) {
        appWidgetIds.forEach { appWidgetId ->
            launch {
                val layoutId = getCorrectLayoutId(appWidgetId, context)
                val remoteView = RemoteViews(context.packageName, layoutId).apply {
                    updateWidget(context, appWidgetId)
                }
                appWidgetManager.updateAppWidget(appWidgetId, remoteView)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetId: Int, extras: Bundle) {
        with(sharedPref) {
            delete(getHeightWidgetKey(appWidgetId))
            delete(getStudentWidgetKey(appWidgetId))
            delete(getThemeWidgetKey(appWidgetId))
            delete(getWidthWidgetKey(appWidgetId))
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetId: Int, newOpts: Bundle?) {
        val remoteView = RemoteViews(context.packageName, getCorrectLayoutId(appWidgetId, context))

        remoteView.setStyles(appWidgetId, newOpts)
        appWidgetManager.updateAppWidget(appWidgetId, remoteView)
    }

    private suspend fun RemoteViews.updateWidget(context: Context, appWidgetId: Int) {
        val luckyNumber = getLuckyNumber(appWidgetId)
        val intent = MainActivity.getStartIntent(context, MainView.Section.LUCKY_NUMBER, true)
        val pendingIntent = PendingIntent.getActivity(
            context, MainView.Section.LUCKY_NUMBER.id, intent, FLAG_UPDATE_CURRENT
        )
        setTextViewText(R.id.luckyNumberWidgetNumber, luckyNumber)
        setOnClickPendingIntent(R.id.luckyNumberWidgetContainer, pendingIntent)

        setStyles(appWidgetId)
    }

    private fun RemoteViews.setStyles(appWidgetId: Int, options: Bundle? = null) {
        val width = options?.getInt(OPTION_APPWIDGET_MIN_WIDTH) ?: sharedPref.getLong(
            getWidthWidgetKey(appWidgetId), 74
        ).toInt()
        val height = options?.getInt(OPTION_APPWIDGET_MAX_HEIGHT) ?: sharedPref.getLong(
            getHeightWidgetKey(appWidgetId), 74
        ).toInt()

        with(sharedPref) {
            putLong(getWidthWidgetKey(appWidgetId), width.toLong())
            putLong(getHeightWidgetKey(appWidgetId), height.toLong())
        }

        val rows = getCellsForSize(height)
        val cols = getCellsForSize(width)

        Timber.d("New lucky number widget measurement: %dx%d", width, height)
        Timber.d("Widget size: $cols x $rows")

        when {
            1 == cols && 1 == rows -> setVisibility(imageTop = false, imageLeft = false)
            1 == cols && 1 < rows -> setVisibility(imageTop = true, imageLeft = false)
            1 < cols && 1 == rows -> setVisibility(imageTop = false, imageLeft = true)
            1 == cols && 1 == rows -> setVisibility(imageTop = true, imageLeft = false)
            2 == cols && 1 == rows -> setVisibility(imageTop = false, imageLeft = true)
            else -> setVisibility(imageTop = false, imageLeft = false, title = true)
        }
    }

    private fun RemoteViews.setVisibility(
        imageTop: Boolean, imageLeft: Boolean, title: Boolean = false
    ) {
        setViewVisibility(R.id.luckyNumberWidgetImageTop, if (imageTop) VISIBLE else GONE)
        setViewVisibility(R.id.luckyNumberWidgetImageLeft, if (imageLeft) VISIBLE else GONE)
        setViewVisibility(R.id.luckyNumberWidgetTitle, if (title) VISIBLE else GONE)
        setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
    }

    private fun getCorrectLayoutId(appWidgetId: Int, context: Context): Int {
        val savedTheme = sharedPref.getLong(getThemeWidgetKey(appWidgetId), 0)
        val isSystemDarkMode = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        return when {
            savedTheme == 1L || savedTheme == 2L && isSystemDarkMode -> R.layout.widget_luckynumber_dark
            else -> R.layout.widget_luckynumber
        }
    }

    private suspend fun getLuckyNumber(appWidgetId: Int): String? {
        return try {
            val student = getStudent(getStudentWidgetKey(appWidgetId)) ?: return "Error"
            val number = luckyNumberRepository.getLuckyNumber(student)

            Timber.d("lucky number: $number")
            if (number == null) {
                val key = getRefreshKey("lucky", student)
                if (refreshHelper.isShouldBeRefreshed(key)) {
                    Timber.d("lucky number refresh: $key")
                    syncManager.startOneTimeSyncWorker(
                        arrayOf(appWidgetId).toIntArray(),
                        LuckyNumberWork::class.java
                    )
                    refreshHelper.updateLastRefreshTimestamp(key)
                    "Loading"
                } else {
                    Timber.d("lucky number already refreshed: $key")
                    "No number"
                }
            } else number.luckyNumber.toString()
        } catch (e: Exception) {
            null
        }
    }
}
