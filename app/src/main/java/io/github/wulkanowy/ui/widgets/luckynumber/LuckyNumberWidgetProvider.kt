package io.github.wulkanowy.ui.widgets.luckynumber

import android.annotation.TargetApi
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import dagger.android.AndroidInjection
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.repositories.luckynumber.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class LuckyNumberWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var semesterRepository: SemesterRepository

    @Inject
    lateinit var luckyNumberRepository: LuckyNumberRepository

    @Inject
    lateinit var schedulers: SchedulersProvider

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    @Inject
    lateinit var sharedPref: SharedPrefHelper

    companion object {
        fun createWidgetKey(appWidgetId: Int) = "lucky_number_widget_$appWidgetId"
    }

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> onUpdate(context, intent)
            AppWidgetManager.ACTION_APPWIDGET_DELETED -> onDelete(intent)
            AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED -> onOptionsChange(context, intent)
        }
    }

    private fun onUpdate(context: Context, intent: Intent) {
        intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS).forEach { appWidgetId ->
            RemoteViews(context.packageName, R.layout.widget_luckynumber).apply {
                setTextViewText(R.id.luckyNumberWidgetNumber, getLuckyNumber()?.luckyNumber?.toString() ?: "Brak")
                setOnClickPendingIntent(R.id.luckyNumberWidgetContainer,
                    PendingIntent.getActivity(context, 2, MainActivity.getStartIntent(context).apply {
                        putExtra(MainActivity.EXTRA_START_MENU_INDEX, 4)
                    }, PendingIntent.FLAG_UPDATE_CURRENT))
            }.also {
                appWidgetManager.updateAppWidget(appWidgetId, it)
            }
        }
    }

    private fun onDelete(intent: Intent) {
        intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0).let {
            if (it != 0) sharedPref.delete(LuckyNumberWidgetProvider.createWidgetKey(it))
        }
    }

    private fun getLuckyNumber(): LuckyNumber? {
        return studentRepository.isStudentSaved()
            .flatMap { studentRepository.getCurrentStudent() }
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMapMaybe { luckyNumberRepository.getLuckyNumber(it) }
            .subscribeOn(schedulers.backgroundThread)
            .blockingGet()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun onOptionsChange(context: Context, intent: Intent) {
        intent.extras?.let { extras ->
            RemoteViews(context.packageName, R.layout.widget_luckynumber).apply {
                setStyles(this, intent)
            }.also {
                appWidgetManager.updateAppWidget(extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID), it)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setStyles(views: RemoteViews, intent: Intent) {
        intent.extras?.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)?.let {
            val maxWidth = it.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            val maxHeight = it.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

            Timber.d("New measurement: ")
            Timber.d("max: %dx%d", maxWidth, maxHeight)

            when {
                // 1x1
                maxWidth < 110 && maxHeight < 110 -> {
                    Timber.d("Size: 1x1")
                    views.setViewVisibility(R.id.luckyNumberWidgetImage, GONE)
                    views.setViewVisibility(R.id.luckyNumberWidgetTitle, GONE)
                    views.setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
                }
                // 1x2
                maxWidth < 110 && maxHeight > 110 -> {
                    Timber.d("Size: 1x2")
                    views.setViewVisibility(R.id.luckyNumberWidgetImage, VISIBLE)
                    views.setViewVisibility(R.id.luckyNumberWidgetTitle, GONE)
                    views.setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
                }
                // 2x1
                maxWidth > 110 && maxHeight < 110 -> {
                    Timber.d("Size: 2x1")
                    views.setViewVisibility(R.id.luckyNumberWidgetImage, GONE)
                    views.setViewVisibility(R.id.luckyNumberWidgetTitle, VISIBLE)
                    views.setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
                }
                // 2x2 and bigger
                else -> {
                    Timber.d("Size: 2x2 and bigger")
                    views.setViewVisibility(R.id.luckyNumberWidgetImage, VISIBLE)
                    views.setViewVisibility(R.id.luckyNumberWidgetTitle, VISIBLE)
                    views.setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
                }
            }
        }
    }
}
