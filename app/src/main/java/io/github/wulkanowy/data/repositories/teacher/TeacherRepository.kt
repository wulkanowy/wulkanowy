package io.github.wulkanowy.data.repositories.teacher

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepository @Inject constructor(
    private val local: TeacherLocal,
    private val remote: TeacherRemote
) {

    suspend fun refreshTeachers(student: Student, semester: Semester) {
        val new = remote.getTeachers(student, semester)
        val old = local.getTeachers(semester).first()

        local.deleteTeachers(old uniqueSubtract new)
        local.saveTeachers(new uniqueSubtract old)
    }

    fun getTeachers(student: Student, semester: Semester): Flow<List<Teacher>> {
        return local.getTeachers(semester).map {
            if (it.isNotEmpty()) return@map it
            refreshTeachers(student, semester)
            it
        }
    }
}
