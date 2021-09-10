package io.github.wulkanowy.services.piggyback

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class VulcanNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
    }
}