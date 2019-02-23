package io.github.wulkanowy.services.sync.channels

import android.annotation.TargetApi
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import io.github.wulkanowy.R
import javax.inject.Inject

@TargetApi(26)
class SyncChannel @Inject constructor(private val appContext: Context) {

    private val notificationManager by lazy { appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    companion object {
        const val CHANNEL_ID = "sync_channel"
    }

    fun create() {
        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, appContext.getString(R.string.channel_sync), IMPORTANCE_HIGH).apply {
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = VISIBILITY_PUBLIC
            })
    }
}
