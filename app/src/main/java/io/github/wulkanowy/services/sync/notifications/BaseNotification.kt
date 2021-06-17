package io.github.wulkanowy.services.sync.notifications

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.data.pojos.Notification
import io.github.wulkanowy.data.pojos.OneNotification
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import kotlin.random.Random

abstract class BaseNotification(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
) {

    protected fun sendNotification(notification: Notification) {
        val linesSize = when (notification) {
            is MultipleNotifications -> notification.lines.size
            is OneNotification -> -1
        }
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            NotificationCompat.Builder(context, notification.channelId)
                .setContentTitle(
                    when (notification) {
                        is OneNotification -> context.getString(notification.titleStringRes)
                        is MultipleNotifications -> context.resources.getQuantityString(
                            notification.titleStringRes, linesSize, linesSize,
                        )
                    }
                )
                .setContentText(
                    when (notification) {
                        is OneNotification -> context.getString(
                            notification.contentStringRes,
                            *notification.contentValues.toTypedArray()
                        )
                        is MultipleNotifications -> context.resources.getQuantityString(
                            notification.contentStringRes, linesSize, linesSize,
                        )
                    }
                )
                .setLargeIcon(
                    context.getCompatBitmap(notification.icon, R.color.colorPrimary)
                )
                .setSmallIcon(R.drawable.ic_stat_all)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, notification.startMenu.id,
                        MainActivity.getStartIntent(context, notification.startMenu, true),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setStyle(NotificationCompat.InboxStyle().run {
                    when (notification) {
                        is MultipleNotifications -> {
                            setSummaryText(
                                context.resources.getQuantityString(
                                    notification.summaryStringRes,
                                    notification.lines.size,
                                    notification.lines.size
                                )
                            )
                            notification.lines.forEach(::addLine)
                        }
                        is OneNotification -> {
                            addLine(
                                context.getString(
                                    notification.contentStringRes,
                                    *notification.contentValues.toTypedArray(),
                                )
                            )
                        }
                    }
                    this
                })
                .build()
        )
    }
}
