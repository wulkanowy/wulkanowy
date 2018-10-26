package io.github.wulkanowy.services.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import timber.log.Timber
import java.util.Random

abstract class BaseNotification(private val context: Context) {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    internal abstract fun createChannel(channelId: String)

    fun notify(notification: Notification) {
        notificationManager.notify(Random().nextInt(1000), notification)
    }

    fun notificationBuilder(channelId: String): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(channelId)
        }
        return NotificationCompat.Builder(context, channelId)
    }

    internal fun getString(@StringRes stringId: Int): String {
        return context.getString(stringId)
    }

    fun getManager(): NotificationManager {
        return notificationManager
    }

    fun cancelAll() {
        notificationManager.cancelAll()
        Timber.d("Notifications canceled")
    }
}
