package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.notifications.NewHomeworkNotification
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import java.time.LocalDate.now
import javax.inject.Inject

class HomeworkWork @Inject constructor(
    private val homeworkRepository: HomeworkRepository,
    private val preferencesRepository: PreferencesRepository,
    private val newHomeworkNotification: NewHomeworkNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        homeworkRepository.getHomework(
            student = student,
            semester = semester,
            start = now().monday,
            end = now().sunday,
            forceRefresh = true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()

        homeworkRepository.getHomeworkFromDatabase(semester, now().monday, now().sunday).first()
            .filter { !it.isNotified }.let {
                if (it.isNotEmpty()) newHomeworkNotification.notify(it)

                homeworkRepository.updateHomework(it.onEach { homework ->
                    homework.isNotified = true
                })
            }
    }

    private fun notify(homework: List<Homework>) {
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            NotificationCompat.Builder(context, NewHomeworkChannel.CHANNEL_ID)
                .setContentTitle(
                    context.resources.getQuantityString(
                        R.plurals.homework_notify_new_item_title, homework.size, homework.size
                    )
                )
                .setContentText(context.resources.getQuantityString(
                    R.plurals.homework_notify_new_item_content,
                    homework.size,
                    homework.size
                ))
                .setSmallIcon(R.drawable.ic_stat_all)
                .setLargeIcon(
                    context.getCompatBitmap(R.drawable.ic_stat_homework, R.color.colorPrimary)
                )
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, MainView.Section.MESSAGE.id,
                        MainActivity.getStartIntent(context, MainView.Section.HOMEWORK, true),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setStyle(NotificationCompat.InboxStyle().run {
                    setSummaryText(
                        context.resources.getQuantityString(
                            R.plurals.homework_number_item, homework.size, homework.size
                        )
                    )
                    homework.forEach { addLine("${it.subject}: ${it.content} - ${it.date}") }
                    this
                })
                .build()
        )
    }
}
