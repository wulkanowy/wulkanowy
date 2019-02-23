package io.github.wulkanowy.services.sync.workers.grade

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.services.sync.channels.SyncChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainActivity.Companion.EXTRA_START_MENU_INDEX
import io.github.wulkanowy.utils.getCompatColor
import javax.inject.Inject

class GradeNotification @Inject constructor(private val appContext: Context) {

    private val notificationManager: NotificationManagerCompat by lazy { NotificationManagerCompat.from(appContext) }

    fun notify(grades: List<Grade>) {
        notificationManager.notify(1, NotificationCompat.Builder(appContext, SyncChannel.CHANNEL_ID)
            .setContentTitle(appContext.resources.getQuantityString(R.plurals.grade_new_items, grades.size, grades.size))
            .setContentText(appContext.resources.getQuantityString(R.plurals.grade_notify_new_items, grades.size, grades.size))
            .setSmallIcon(R.drawable.ic_stat_notify_grade)
            .setAutoCancel(true)
            .setPriority(PRIORITY_HIGH)
            .setColor(appContext.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(appContext, 0,
                    MainActivity.getStartIntent(appContext).putExtra(EXTRA_START_MENU_INDEX, 0), FLAG_UPDATE_CURRENT))
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(appContext.resources.getQuantityString(R.plurals.grade_number_item, grades.size, grades.size))
                grades.forEach { addLine("${it.subject}: ${it.entry}") }
                this
            })
            .build()
        )
    }
}

