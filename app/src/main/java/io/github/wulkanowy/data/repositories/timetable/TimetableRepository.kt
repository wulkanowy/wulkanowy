package io.github.wulkanowy.data.repositories.timetable

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: TimetableLocal,
    private val remote: TimetableRemote,
    private val schedulerHelper: TimetableNotificationSchedulerHelper
) {

    suspend fun getTimetable(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean = false): List<Timetable> {
        val monday = start.monday
        val sunday = start.sunday

        return local.getTimetable(semester, monday, sunday).filter { !forceRefresh }.ifEmpty {
            val new = remote.getTimetable(student, semester, monday, sunday)
            val old = local.getTimetable(semester, monday, sunday)

            local.deleteTimetable(old.uniqueSubtract(new).also { schedulerHelper.cancelScheduled(it) })
            local.saveTimetable(new.uniqueSubtract(old).also { schedulerHelper.scheduleNotifications(it, student) }.map { item ->
                item.also { new ->
                    old.singleOrNull { new.start == it.start }?.let { old ->
                        return@map new.copy(
                            room = if (new.room.isEmpty()) old.room else new.room,
                            teacher = if (new.teacher.isEmpty() && !new.changes && !old.changes) old.teacher else new.teacher
                        )
                    }
                }
            })

            return local.getTimetable(semester, monday, sunday)
                .filter { it.date in start..end }
                .also { schedulerHelper.scheduleNotifications(it, student) }
        }
    }
}
