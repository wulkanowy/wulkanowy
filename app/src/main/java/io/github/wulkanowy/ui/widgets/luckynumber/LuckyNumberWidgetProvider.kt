package io.github.wulkanowy.ui.widgets.luckynumber

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import javax.inject.Inject

class LuckyNumberWidgetProvider : BroadcastReceiver() {

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
        }
    }

    private fun onUpdate(context: Context, intent: Intent) {
        intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS).forEach { appWidgetId ->
            RemoteViews(context.packageName, R.layout.widget_luckynumber).apply {
                setTextViewText(R.id.luckyNumberWidgetNumber, getLuckyNumber()?.luckyNumber?.toString() ?: context.getString(R.string.lucky_number_empty))
                setPendingIntentTemplate(R.id.luckyNumberWidgetContainer,
                    PendingIntent.getActivity(context, 1, MainActivity.getStartIntent(context).apply {
                        putExtra(MainActivity.EXTRA_START_MENU_INDEX, 4)
                    }, PendingIntent.FLAG_UPDATE_CURRENT))
            }.also {
                appWidgetManager.apply {
                    notifyAppWidgetViewDataChanged(appWidgetId, R.id.luckyNumberWidgetContainer)
                    updateAppWidget(appWidgetId, it)
                }
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
}
