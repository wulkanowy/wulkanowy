package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.notifications.NewExamNotification
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import java.time.LocalDate.now
import javax.inject.Inject

class ExamWork @Inject constructor(
    private val examRepository: ExamRepository,
    private val preferencesRepository: PreferencesRepository,
    private val newExamNotification: NewExamNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        examRepository.getExams(
            student = student,
            semester = semester,
            start = now(),
            end = now(),
            forceRefresh = true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()

        examRepository.getExamsFromDatabase(semester, now()).first()
            .filter { !it.isNotified }.let {
                if (it.isNotEmpty()) newExamNotification.notify(it)

            examRepository.updateExam(it.onEach { exam -> exam.isNotified = true })
        }
    }

    private fun notify(exam: List<Exam>) {
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            NotificationCompat.Builder(context, NewExamChannel.CHANNEL_ID)
                .setContentTitle(
                    context.resources.getQuantityString(
                        R.plurals.exam_notify_new_item_title, exam.size, exam.size
                    )
                )
                .setContentText(context.resources.getQuantityString(
                    R.plurals.exam_notify_new_item_content,
                    exam.size,
                    exam.size
                ))
                .setSmallIcon(R.drawable.ic_stat_all)
                .setLargeIcon(
                    context.getCompatBitmap(R.drawable.ic_stat_exam, R.color.colorPrimary)
                )
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, MainView.Section.MESSAGE.id,
                        MainActivity.getStartIntent(context, MainView.Section.EXAM, true),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setStyle(NotificationCompat.InboxStyle().run {
                    setSummaryText(
                        context.resources.getQuantityString(
                            R.plurals.exam_number_item,
                            exam.size,
                            exam.size
                        )
                    )
                    exam.forEach { addLine("${it.subject}: ${it.description} - ${it.date}") }
                    this
                })
                .build()
        )
    }
}
