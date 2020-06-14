package io.github.wulkanowy.data.repositories.subject

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: SubjectLocal,
    private val remote: SubjectRemote
) {

    suspend fun getSubjects(student: Student, semester: Semester, forceRefresh: Boolean = false): List<Subject> {
        return local.getSubjects(semester).filter { !forceRefresh }.ifEmpty {
            val new = remote.getSubjects(student, semester)
            val old = local.getSubjects(semester)

            local.deleteSubjects(old.uniqueSubtract(new))
            local.saveSubjects(new.uniqueSubtract(old))

            return local.getSubjects(semester)
        }
    }
}
