package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.NewExamChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import java.time.LocalDate.now
import javax.inject.Inject
import kotlin.random.Random

class ExamWork @Inject constructor(
    @ApplicationContext private val context: Context,
    private val examRepository: ExamRepository,
    private val notificationManager: NotificationManagerCompat,
    private val preferencesRepository: PreferencesRepository
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

        examRepository.getNotNotifiedExam(semester, now()).first().let {
            if (it.isNotEmpty()) notify(it)

            examRepository.updateExam(it.onEach { exam -> exam.isNotified = true })
        }
    }

    private fun notify(exam: List<Exam>) {
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_main_exam)!!.mutate()
        icon.colorFilter =
            PorterDuffColorFilter(context.resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            NotificationCompat.Builder(context, NewExamChannel.CHANNEL_ID)
                .setContentTitle(
                    context.resources.getQuantityString(
                        R.plurals.exam_notify_new_item_title,
                        exam.size,
                        exam.size
                    )
                )
                .setSmallIcon(R.drawable.ic_stat_push)
                .setLargeIcon(icon.toBitmap())
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
                    exam.forEach { addLine("${it.subject}: ${it.description}") }
                    this
                })
                .build()
        )
    }
}
