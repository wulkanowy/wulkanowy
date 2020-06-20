package io.github.wulkanowy.data.repositories.attendancesummary

import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryRepository @Inject constructor(
    private val local: AttendanceSummaryLocal,
    private val remote: AttendanceSummaryRemote
) {

    suspend fun refreshAttendanceSummary(student: Student, semester: Semester, subjectId: Int) {
        val new = remote.getAttendanceSummary(student, semester, subjectId)
        val old = local.getAttendanceSummary(semester, subjectId).first()

        local.deleteAttendanceSummary(old uniqueSubtract new)
        local.saveAttendanceSummary(new uniqueSubtract old)
    }

    fun getAttendanceSummary(student: Student, semester: Semester, subjectId: Int): Flow<List<AttendanceSummary>> {
        return local.getAttendanceSummary(semester, subjectId).map {
            if (it.isNotEmpty()) return@map it
            refreshAttendanceSummary(student, semester, subjectId)
            it
        }
    }
}
