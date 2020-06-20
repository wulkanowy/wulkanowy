package io.github.wulkanowy.data.repositories.exam

import io.github.wulkanowy.data.db.entities.Exam
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
class ExamRepository @Inject constructor(
    private val local: ExamLocal,
    private val remote: ExamRemote
) {

    suspend fun refreshExams(student: Student, semester: Semester, start: LocalDate, end: LocalDate) {
        val new = remote.getExams(student, semester, start.monday, end.sunday)
        val old = local.getExams(semester, start.monday, end.sunday).first()

        local.deleteExams(old uniqueSubtract new)
        local.saveExams(new uniqueSubtract old)
    }

    fun getExams(student: Student, semester: Semester, start: LocalDate, end: LocalDate): Flow<List<Exam>> {
        return local.getExams(semester, start.monday, end.sunday)
            .map { it.filter { item -> item.date in start..end } }
            .map {
                if (it.isNotEmpty()) return@map it
                refreshExams(student, semester, start, end)
                it
            }
    }
}
