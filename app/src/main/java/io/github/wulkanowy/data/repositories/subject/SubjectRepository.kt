package io.github.wulkanowy.data.repositories.subject

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val local: SubjectLocal,
    private val remote: SubjectRemote
) {

    private suspend fun refreshSubjects(student: Student, semester: Semester) {
        val new = remote.getSubjects(student, semester)
        val old = local.getSubjects(semester).first()

        local.deleteSubjects(old uniqueSubtract new)
        local.saveSubjects(new uniqueSubtract old)
    }

    fun getSubjects(student: Student, semester: Semester): Flow<List<Subject>> {
        return local.getSubjects(semester).map {
            if (it.isNotEmpty()) return@map it
            refreshSubjects(student, semester)
            it
        }
    }
}
