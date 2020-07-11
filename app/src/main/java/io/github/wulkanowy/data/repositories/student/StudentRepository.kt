package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.utils.flowWithResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val local: StudentLocal,
    private val remote: StudentRemote
) {

    suspend fun isStudentSaved(): Boolean = local.getStudents(false).isNotEmpty()

    suspend fun isCurrentStudentSet(): Boolean = local.getCurrentStudent(false)?.isCurrent ?: false

    fun getStudentsApi(pin: String, symbol: String, token: String) = flowWithResource {
        remote.getStudentsMobileApi(token, pin, symbol)
    }

    fun getStudentsScrapper(email: String, password: String, endpoint: String, symbol: String) = flowWithResource {
        remote.getStudentsScrapper(email, password, endpoint, symbol)
    }

    fun getStudentsHybrid(email: String, password: String, endpoint: String, symbol: String) = flowWithResource {
        remote.getStudentsHybrid(email, password, endpoint, symbol)
    }

    suspend fun getSavedStudents(decryptPass: Boolean = true): List<Student> {
        return local.getStudents(decryptPass)
    }

    suspend fun getStudentById(id: Int): Student {
        return local.getStudentById(id) ?: throw NoCurrentStudentException()
    }

    suspend fun getCurrentStudent(decryptPass: Boolean = true): Student {
        return local.getCurrentStudent(decryptPass) ?: throw NoCurrentStudentException()
    }

    suspend fun saveStudents(students: List<Student>): List<Long> {
        return local.saveStudents(students)
    }

    suspend fun switchStudent(student: Student) {
        return local.setCurrentStudent(student)
    }

    suspend fun logoutStudent(student: Student) {
        return local.logoutStudent(student)
    }
}
