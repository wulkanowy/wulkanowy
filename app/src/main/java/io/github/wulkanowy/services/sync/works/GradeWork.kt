package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.channels.SyncChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainActivity.Companion.EXTRA_START_MENU_INDEX
import io.github.wulkanowy.utils.getCompatColor
import io.reactivex.Completable
import javax.inject.Inject

class GradeWork @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val gradeRepository: GradeRepository,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override fun create(): Completable {
        return studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it).map { semester -> it to semester } }
            .flatMap { data ->
                gradeRepository.getGrades(data.first, data.second, true, preferencesRepository.isNotificationsEnable)
                    .flatMap { gradeRepository.getUnnotifiedGrades(data.second) }
            }
            .flatMapCompletable {
                if (it.isNotEmpty()) notify(it)
                gradeRepository.updateGrades(it.onEach { grade -> grade.isNotified = true })
            }
    }

    private fun notify(grades: List<Grade>) {
        notificationManager.notify(1, NotificationCompat.Builder(context, SyncChannel.CHANNEL_ID)
            .setContentTitle(context.resources.getQuantityString(R.plurals.grade_new_items, grades.size, grades.size))
            .setContentText(context.resources.getQuantityString(R.plurals.grade_notify_new_items, grades.size, grades.size))
            .setSmallIcon(R.drawable.ic_stat_notify_grade)
            .setAutoCancel(true)
            .setPriority(PRIORITY_HIGH)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(context, 0,
                    MainActivity.getStartIntent(context).putExtra(EXTRA_START_MENU_INDEX, 0), FLAG_UPDATE_CURRENT))
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.grade_number_item, grades.size, grades.size))
                grades.forEach { addLine("${it.subject}: ${it.entry}") }
                this
            })
            .build()
        )
    }
}

