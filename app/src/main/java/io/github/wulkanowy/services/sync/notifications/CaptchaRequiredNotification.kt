package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.ui.modules.Destination
import java.time.Instant
import javax.inject.Inject
import kotlin.time.toJavaDuration

class CaptchaRequiredNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    private val preferencesRepository: PreferencesRepository,
    @ApplicationContext private val context: Context,
) {
    suspend fun notify(student: Student) {
        // Notification disabled
        if (!preferencesRepository.captchaRequiredNotificationEnabled) return
        // User didn't solve the captcha since last notification
        if (preferencesRepository.sentCaptchaNotification) return

        val minTimeBetweenNotifications =
            preferencesRepository.minTimeBetweenCaptchaRequiredNotification
        val lastNotification =
            preferencesRepository.lastCaptchaRequiredNotificationTime ?: Instant.MIN
        if (Instant.now()
                .isBefore(lastNotification + minTimeBetweenNotifications.toJavaDuration())
        ) {
            // Too little time passed between notifications
            return
        }

        val notificationData = NotificationData(
            title = context.getString(R.string.captcha_required_notify_title),
            content = context.getString(R.string.captcha_required_notify_content),
            destination = Destination.Dashboard,
        )
        if (appNotificationManager.trySendSingletonNotification(
            notificationData, NotificationType.CAPTCHA_REQUIRED, student
            )
        ) {
            preferencesRepository.lastCaptchaRequiredNotificationTime = Instant.now()
            preferencesRepository.sentCaptchaNotification = true
        }
    }
}
