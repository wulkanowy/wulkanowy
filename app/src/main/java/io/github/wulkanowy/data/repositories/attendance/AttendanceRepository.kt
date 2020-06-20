package io.github.wulkanowy.data.repositories.attendance

import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
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
class AttendanceRepository @Inject constructor(
    private val local: AttendanceLocal,
    private val remote: AttendanceRemote
) {

    suspend fun refreshAttendance(student: Student, semester: Semester, start: LocalDate, end: LocalDate) {
        val new = remote.getAttendance(student, semester, start.monday, end.sunday)
        val old = local.getAttendance(semester, start.monday, end.sunday).first()
        local.deleteAttendance(old uniqueSubtract new)
        local.saveAttendance(new uniqueSubtract old)
    }

    fun getAttendance(student: Student, semester: Semester, start: LocalDate, end: LocalDate): Flow<List<Attendance>> {
        return local.getAttendance(semester, start.monday, end.sunday)
            .map { it.filter { item -> item.date in start..end } }
            .map {
                if (it.isNotEmpty()) return@map it
                refreshAttendance(student, semester, start, end)
                it
            }
    }

    suspend fun excuseForAbsence(student: Student, semester: Semester, attendanceList: List<Attendance>, reason: String? = null): Boolean {
        return remote.excuseAbsence(student, semester, attendanceList, reason)
    }
}
