package io.github.wulkanowy.services.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.main.MainActivity
import timber.log.Timber

class GradeNotification(private val context: Context) : BaseNotification(context) {

    override val channelId = "Grade_Notify"

    @TargetApi(26)
    override fun createChannel() {
        getManager().createNotificationChannel(NotificationChannel(
            channelId, getString(R.string.notify_grade_channel),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        })
    }

    fun sendNotification(title: String, content: String) {
        notify(
            notificationBuilder()
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_stat_notify_grade)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.resources.getColor(R.color.colorPrimary))
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, 0,
                        MainActivity.getStartIntent(context),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .build()
        )

        Timber.d("Notification sent")
    }
}
