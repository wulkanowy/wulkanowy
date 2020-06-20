package io.github.wulkanowy.data.repositories.completedlessons

import io.github.wulkanowy.data.db.entities.CompletedLesson
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
class CompletedLessonsRepository @Inject constructor(
    private val local: CompletedLessonsLocal,
    private val remote: CompletedLessonsRemote
) {

    suspend fun refreshCompletedLessons(student: Student, semester: Semester, start: LocalDate, end: LocalDate) {
        val new = remote.getCompletedLessons(student, semester, start.monday, end.sunday)
        val old = local.getCompletedLessons(semester, start.monday, end.sunday).first()

        local.deleteCompleteLessons(old.uniqueSubtract(new))
        local.saveCompletedLessons(new.uniqueSubtract(old))
    }

    suspend fun getCompletedLessons(student: Student, semester: Semester, start: LocalDate, end: LocalDate): Flow<List<CompletedLesson>> {
        return local.getCompletedLessons(semester, start.monday, end.sunday)
            .map { it.filter { item -> item.date in start..end } }
            .map {
                if (it.isNotEmpty()) return@map it
                refreshCompletedLessons(student, semester, start, end)
                it
            }
    }
}
