package io.github.wulkanowy.data.repositories.school

import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolRepository @Inject constructor(
    private val local: SchoolLocal,
    private val remote: SchoolRemote
) {

    suspend fun refreshSchool(student: Student, semester: Semester) {
        val new = remote.getSchoolInfo(student, semester)
        val old = local.getSchool(semester).first()

        if (new != old && old != null) {
            local.deleteSchool(old)
            local.saveSchool(new)
        }
        local.saveSchool(new)
    }

    fun getSchoolInfo(student: Student, semester: Semester): Flow<School?> {
        return local.getSchool(semester).map {
            if (it != null) return@map it
            refreshSchool(student, semester)
            it
        }
    }
}
