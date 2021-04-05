package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.LuckyNumberChannel
import io.github.wulkanowy.ui.base.BaseAppWidgetProvider.Companion.WIDGET_IDS_KEY
import io.github.wulkanowy.ui.base.BaseAppWidgetProvider.Companion.WIDGET_UPDATE_STATUS
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetProvider
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetProvider.Companion.UPDATE_REFRESH_ERROR_KEY
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetProvider.Companion.UPDATE_REFRESH_KEY
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.waitForResult
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

class LuckyNumberWork @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val luckyNumberRepository: LuckyNumberRepository,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester, params: WorkerParameters) {
        luckyNumberRepository.getLuckyNumber(
            student, true, preferencesRepository.isNotificationsEnable
        ).waitForResult()

        Timber.d("lucky number worker result")
        updateWidget(params, UPDATE_REFRESH_KEY)

        luckyNumberRepository.getNotNotifiedLuckyNumber(student)?.let {
            notify(it)
            luckyNumberRepository.updateLuckyNumber(it.apply { isNotified = true })
        }
    }

    override suspend fun onFailure(params: WorkerParameters, e: Throwable) {
        updateWidget(params, UPDATE_REFRESH_ERROR_KEY)
    }

    private fun notify(luckyNumber: LuckyNumber) {
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            NotificationCompat.Builder(context, LuckyNumberChannel.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.lucky_number_notify_new_item_title))
                .setContentText(
                    context.getString(
                        R.string.lucky_number_notify_new_item,
                        luckyNumber.luckyNumber
                    )
                )
                .setSmallIcon(R.drawable.ic_stat_luckynumber)
                .setAutoCancel(true)
                .setDefaults(DEFAULT_ALL)
                .setPriority(PRIORITY_HIGH)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        MainView.Section.MESSAGE.id,
                        MainActivity.getStartIntent(context, MainView.Section.LUCKY_NUMBER, true),
                        FLAG_UPDATE_CURRENT
                    )
                )
                .build()
        )
    }

    private fun updateWidget(params: WorkerParameters, status: String) {
        context.sendBroadcast(Intent(context, LuckyNumberWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(WIDGET_UPDATE_STATUS, status)
            putExtra(WIDGET_IDS_KEY, params.inputData.getIntArray(WIDGET_IDS_KEY))
        })
    }
}
