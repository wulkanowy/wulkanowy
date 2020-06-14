package io.github.wulkanowy.data.repositories.homework

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: HomeworkLocal,
    private val remote: HomeworkRemote
) {

    suspend fun getHomework(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean = false): List<Homework> {
        val monday = start.monday
        val friday = end.sunday

        return local.getHomework(semester, monday, friday).filter { !forceRefresh }.ifEmpty {
            val new = remote.getHomework(student, semester, monday, friday)

            val old = local.getHomework(semester, monday, friday)

            local.deleteHomework(old.uniqueSubtract(new))
            local.saveHomework(new.uniqueSubtract(old))

            return local.getHomework(semester, monday, friday)
        }
    }

    suspend fun toggleDone(homework: Homework) {
        return local.updateHomework(listOf(homework.apply {
            isDone = !isDone
        }))
    }
}
