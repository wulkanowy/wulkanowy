package io.github.wulkanowy.services.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat

import java.util.Random

abstract class BaseNotification(private val context: Context) {

    abstract val channelId: String

    private var manager: NotificationManager? = null

    @TargetApi(26)
    abstract fun createChannel()

    fun notify(notification: Notification) {
        getManager().notify(Random().nextInt(1000), notification)
    }

    fun notificationBuilder(): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        return NotificationCompat.Builder(context, channelId)
    }

    internal fun getManager(): NotificationManager {
        if (null == manager) {
            manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager as NotificationManager
    }

    internal fun getString(@StringRes stringId: Int): String {
        return context.getString(stringId)
    }

    fun cancelAll() {
        getManager().cancelAll()
    }
}
