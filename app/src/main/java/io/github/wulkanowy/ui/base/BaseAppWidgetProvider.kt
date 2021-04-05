package io.github.wulkanowy.ui.base

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.utils.AutoRefreshHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * @see [android.appwidget.AppWidgetProvider]
 */
abstract class BaseAppWidgetProvider : BroadcastReceiver(), CoroutineScope {

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var sharedPref: SharedPrefProvider

    @Inject
    lateinit var refreshHelper: AutoRefreshHelper

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    companion object {
        const val WIDGET_UPDATE_STATUS = "widget_update_status"
        const val WIDGET_IDS_KEY = AppWidgetManager.EXTRA_APPWIDGET_IDS
    }

    fun Bundle.toPrintableString(): String {
        return keySet().map {
            it to get(it)
        }.map { (key, value) ->
            key to when (value) {
                is IntArray -> value.toList()
                else -> value
            }
        }.joinToString(", ") { (key, value) ->
            "$key: $value"
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("OnReceive $intent (extras: ${intent.extras?.toPrintableString()})")
        val extras = intent.extras

        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val appWidgetIds = extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                    onUpdate(context, appWidgetIds, extras)
                }
            }
            AppWidgetManager.ACTION_APPWIDGET_DELETED -> {
                if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                    val appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
                    onDeleted(context, appWidgetId, extras)
                }
            }
            AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED -> {
                if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)
                    && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)
                ) {
                    val appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
                    val widgetExtras = extras.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)
                    onAppWidgetOptionsChanged(context, appWidgetId, widgetExtras)
                }
            }
            AppWidgetManager.ACTION_APPWIDGET_ENABLED -> onEnabled(context)
            AppWidgetManager.ACTION_APPWIDGET_DISABLED -> onDisabled(context)
            AppWidgetManager.ACTION_APPWIDGET_RESTORED -> {
                val oldIds = extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_OLD_IDS)
                val newIds = extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (oldIds != null && oldIds.isNotEmpty() && newIds != null && newIds.isNotEmpty()) {
                    onRestored(context, oldIds, newIds)
                    onUpdate(context, newIds, extras)
                }
            }
        }
    }

    fun getCellsForSize(size: Int): Int {
        var n = 2
        while (74 * n - 30 < size) ++n
        return n - 1
    }

    protected suspend fun getStudent(widgetKey: String): Student? {
        return try {
            val studentId = sharedPref.getLong(widgetKey, 0)
            val students = studentRepository.getSavedStudents()
            val student = students.singleOrNull { it.student.id == studentId }?.student
            when {
                student != null -> student
                studentId != 0L && studentRepository.isCurrentStudentSet() -> {
                    studentRepository.getCurrentStudent(false).also {
                        sharedPref.putLong(widgetKey, it.id)
                    }
                }
                else -> null
            }
        } catch (e: Throwable) {
            if (e.cause !is NoCurrentStudentException) {
                Timber.e(e, "An error has occurred in lucky number provider")
            }
            null
        }
    }

    open fun onUpdate(context: Context, appWidgetIds: IntArray, extras: Bundle?) {}
    open fun onAppWidgetOptionsChanged(context: Context, appWidgetId: Int, newOpts: Bundle?) {}
    open fun onEnabled(context: Context) {}
    open fun onDisabled(context: Context) {}
    open fun onDeleted(context: Context, appWidgetId: Int, extras: Bundle) {}
    open fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray?) {}
}
