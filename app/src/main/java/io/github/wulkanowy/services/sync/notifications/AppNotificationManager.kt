package io.github.wulkanowy.services.sync.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotificationsData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.data.pojos.OneNotificationData
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.nickOrName
import javax.inject.Inject
import kotlin.random.Random

class AppNotificationManager @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    @ApplicationContext private val context: Context,
    private val appInfo: AppInfo
) {

    fun sendNotification(notificationData: NotificationData, student: Student) =
        when (notificationData) {
            is OneNotificationData -> sendOneNotification(notificationData, student)
            is MultipleNotificationsData -> sendMultipleNotifications(notificationData, student)
        }

    private fun sendOneNotification(notificationData: OneNotificationData, student: Student?) {
        val content = context.getString(
            notificationData.contentStringRes,
            *notificationData.contentValues.toTypedArray()
        )

        val notification = getDefaultNotificationBuilder(notificationData)
            .setContentTitle(context.getString(notificationData.titleStringRes))
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setSummaryText(student?.nickOrName)
                    .bigText(content)
            )
            .build()

        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), notification)
    }

    private fun sendMultipleNotifications(
        notificationData: MultipleNotificationsData,
        student: Student
    ) {
        val group = notificationData.type.group + student.id
        val groupId = student.id * 100 + notificationData.type.ordinal

        notificationData.lines.forEach { item ->
            val notification = getDefaultNotificationBuilder(notificationData)
                .setContentTitle(
                    context.resources.getQuantityString(notificationData.titleStringRes, 1)
                )
                .setContentText(item)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setSummaryText(student.nickOrName)
                        .bigText(item)
                )
                .setGroup(group)
                .build()

            notificationManager.notify(Random.nextInt(Int.MAX_VALUE), notification)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        val summaryNotification = getDefaultNotificationBuilder(notificationData)
            .setSmallIcon(notificationData.icon)
            .setGroup(group)
            .setStyle(NotificationCompat.InboxStyle().setSummaryText(student.nickOrName))
            .setGroupSummary(true)
            .build()

        notificationManager.notify(groupId.toInt(), summaryNotification)
    }

    @SuppressLint("InlinedApi")
    private fun getDefaultNotificationBuilder(notificationData: NotificationData): NotificationCompat.Builder {
        val pendingIntentsFlags = if (appInfo.systemVersion >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return NotificationCompat.Builder(context, notificationData.type.channel)
            .setLargeIcon(context.getCompatBitmap(notificationData.icon, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_stat_all)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(
                    context, notificationData.startMenu.id,
                    MainActivity.getStartIntent(context, notificationData.startMenu, true),
                    pendingIntentsFlags
                )
            )
    }
}