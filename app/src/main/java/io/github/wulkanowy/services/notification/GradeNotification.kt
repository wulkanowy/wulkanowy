package io.github.wulkanowy.services.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import io.github.wulkanowy.BuildConfig.APPLICATION_ID
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainActivity.Companion.EXTRA_CARD_ID_KEY
import timber.log.Timber

class GradeNotification(private val context: Context) : BaseNotification(context) {

    private val channelId = "Grade_Notify"

    @TargetApi(26)
    override fun createChannel(channelId: String) {
        getManager().createNotificationChannel(NotificationChannel(
            channelId, getString(R.string.notify_grade_channel),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        })
    }

    fun sendNotification(id: Int, title: String, content: String) {
        notify(
            id = id,
            notification = notificationBuilder(channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_stat_notify_grade)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.resources.getColor(R.color.colorPrimary))
                .setGroup("$APPLICATION_ID.NEW_GRADES")
                .setLights(
                    0xFF0000,
                    context.resources.getInteger(context.resources.getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                    context.resources.getInteger(context.resources.getIdentifier("config_defaultNotificationLedOff", "integer", "android"))
                )
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, 0,
                        MainActivity.getStartIntent(context).putExtra(EXTRA_CARD_ID_KEY, 0),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .build()
        )

        Timber.d("Notification sent")
    }

    fun cancelNotification(id: Int) {
        getManager().cancel(id)
    }
}
