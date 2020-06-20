package io.github.wulkanowy.data.repositories.homework

import io.github.wulkanowy.data.db.entities.Homework
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
class HomeworkRepository @Inject constructor(
    private val local: HomeworkLocal,
    private val remote: HomeworkRemote
) {

    suspend fun refreshHomework(student: Student, semester: Semester, start: LocalDate, end: LocalDate) {
        val new = remote.getHomework(student, semester, start.monday, end.sunday)
        val old = local.getHomework(semester, start.monday, end.sunday).first()

        local.deleteHomework(old uniqueSubtract new)
        local.saveHomework(new uniqueSubtract old)
    }

    suspend fun getHomework(student: Student, semester: Semester, start: LocalDate, end: LocalDate): Flow<List<Homework>> {
        return local.getHomework(semester, start.monday, end.sunday).map {
            if (it.isNotEmpty()) return@map it
            refreshHomework(student, semester, start, end)
            it
        }
    }

    suspend fun toggleDone(homework: Homework) {
        local.updateHomework(listOf(homework.apply {
            isDone = !isDone
        }))
    }
}
