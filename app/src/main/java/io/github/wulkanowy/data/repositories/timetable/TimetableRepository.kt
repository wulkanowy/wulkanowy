package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRepository @Inject constructor(
    private val local: TimetableLocal,
    private val remote: TimetableRemote,
    private val schedulerHelper: TimetableNotificationSchedulerHelper
) {

    suspend fun refreshTimetable(student: Student, semester: Semester, start: LocalDate, end: LocalDate) {
        val new = remote.getTimetable(student, semester, start.monday, end.sunday)
        val old = local.getTimetable(semester, start.monday, end.sunday).first()

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
    }

    fun getTimetable(student: Student, semester: Semester, start: LocalDate, end: LocalDate): Flow<List<Timetable>> {
        return local.getTimetable(semester, start.monday, end.sunday)
            .map { it.filter { item -> item.date in start..end } }
            .map {
                if (it.isNotEmpty()) return@map it
                refreshTimetable(student, semester, start, end)
                it
            }.map { schedulerHelper.scheduleNotifications(it, student); it }
    }
}
