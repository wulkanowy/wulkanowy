package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.AttendanceDao
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.utils.extension.toDate
import io.reactivex.Maybe
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters
import javax.inject.Inject

class AttendanceLocal @Inject constructor(private val attendanceDb: AttendanceDao) {

    fun getAttendance(semester: Semester, startDate: LocalDate): Maybe<List<Attendance>> {
        return attendanceDb.getExams(semester.diaryId, semester.studentId, startDate.toDate(),
                startDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).toDate()
        ).filter { !it.isEmpty() }
    }

    fun saveAttendance(attendance: List<Attendance>) {
        attendanceDb.insertAll(attendance)
    }

    fun deleteAttendance(attendance: List<Attendance>) {
        attendanceDb.deleteAll(attendance)
    }
}
