package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.SchoolAnnouncementRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.NewSchoolAnnouncementsChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.random.Random

class SchoolAnnouncementWork @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schoolAnnouncementRepository: SchoolAnnouncementRepository,
    private val notificationManager: NotificationManagerCompat,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        schoolAnnouncementRepository.getSchoolAnnouncements(
            student,
            true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()


        schoolAnnouncementRepository.getNotNotifiedSchoolAnnouncement(semester).first().let {
            if (it.isNotEmpty()) notify(it)

            schoolAnnouncementRepository.updateSchoolAnnouncement(it.onEach { schoolAnnouncement -> schoolAnnouncement.isNotified = true })
    }
}

    private fun notify(schoolAnnouncement: List<SchoolAnnouncement>) {
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            NotificationCompat.Builder(context, NewSchoolAnnouncementsChannel.CHANNEL_ID)
                .setContentTitle(context.resources.getQuantityString(
                        R.plurals.school_announcement_notify_new_item_title,
                        schoolAnnouncement.size,
                        schoolAnnouncement.size
                    ))
                .setContentText(context.resources.getQuantityString(R.plurals.school_announcement_notify_new_items, schoolAnnouncement.size, schoolAnnouncement.size))
                .setSmallIcon(R.drawable.ic_stat_all)
                .setLargeIcon(context.getCompatBitmap(R.drawable.ic_all_about, R.color.colorPrimary))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, MainView.Section.SCHOOL_ANNOUNCEMENT.id,
                        MainActivity.getStartIntent(context, MainView.Section.SCHOOL_ANNOUNCEMENT, true),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setStyle(NotificationCompat.InboxStyle().run {
                    setSummaryText(
                        context.resources.getQuantityString(
                            R.plurals.school_announcement_number_item,
                            schoolAnnouncement.size,
                            schoolAnnouncement.size
                        )
                    )
                    schoolAnnouncement.forEach { addLine("${it.subject}: ${it.content}") }
                    this
                })
                .build()
        )
    }
}
