package io.github.wulkanowy.services.sync

import android.content.Context
import androidx.core.app.NotificationManagerCompat

class BaseNotification(protected val appContext: Context) {

    protected val notifacationManager by lazy { NotificationManagerCompat.from(appContext) }
}
