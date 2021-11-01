package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.pojos.GroupNotificationData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.getPlural
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDate
import javax.inject.Inject

class ChangeTimetableNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    @ApplicationContext private val context: Context,
) {

    suspend fun notify(items: List<Timetable>, student: Student) {
        val changedLessons = items.filter { it.canceled || it.changes }
        val lines = changedLessons.groupBy { it.date }
            .map { (date, lessons) -> getNotificationContent(date, lessons) }
            .flatten()
            .ifEmpty { return }

        val notificationDataList = lines.map {
            NotificationData(
                title = context.getPlural(R.plurals.timetable_notify_new_items_title, 1),
                content = it,
                intentToStart = MainActivity.getStartIntent(
                    context,
                    Destination.Timetable(LocalDate.now().minusDays(5)),
                    true
                )
            )
        }

        val groupNotificationData = GroupNotificationData(
            notificationDataList = notificationDataList,
            title = context.getPlural(
                R.plurals.timetable_notify_new_items_title,
                changedLessons.size
            ),
            content = context.getPlural(
                R.plurals.timetable_notify_new_items_group,
                changedLessons.size,
                changedLessons.size
            ),
            intentToStart = Intent(),
            type = NotificationType.CHANGE_TIMETABLE
        )

        appNotificationManager.sendMultipleNotifications(groupNotificationData, student)
    }

    private fun getNotificationContent(date: LocalDate, lessons: List<Timetable>): List<String> {
        val formattedDate = date.toFormattedString("EEE dd.MM")

        return if (lessons.size > 2) {
            listOf(
                context.getPlural(
                    R.plurals.timetable_notify_new_items,
                    lessons.size,
                    formattedDate,
                    lessons.size,
                )
            )
        } else {
            lessons.map {
                var text = context.getString(
                    R.string.timetable_notify_lesson,
                    formattedDate,
                    it.number,
                    it.subject
                )

                if (it.roomOld.isNotBlank()) {
                    text += context.getString(
                        R.string.timetable_notify_change_room,
                        it.roomOld,
                        it.room
                    )
                }
                if (it.teacherOld.isNotBlank() && it.teacher != it.teacherOld) {
                    text += context.getString(
                        R.string.timetable_notify_change_teacher,
                        it.teacherOld,
                        it.teacher
                    )
                }
                if (it.subjectOld.isNotBlank()) {
                    text += context.getString(
                        R.string.timetable_notify_change_subject,
                        it.subjectOld,
                        it.subject
                    )
                }

                text += if (it.info.isNotBlank()) "\n${it.info}" else ""
                text
            }
        }
    }
}
