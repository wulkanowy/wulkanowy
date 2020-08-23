package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.pojos.StudentAndSemesters
import io.github.wulkanowy.data.repositories.semester.SemesterLocal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val local: StudentLocal,
    private val semestersLocal: SemesterLocal,
    private val remote: StudentRemote
) {

    suspend fun isStudentSaved(): Boolean = local.getStudents(false).isNotEmpty()

    suspend fun isCurrentStudentSet(): Boolean = local.getCurrentStudent(false)?.isCurrent ?: false

    suspend fun getStudentsApi(pin: String, symbol: String, token: String): List<StudentAndSemesters> {
        return remote.getStudentsMobileApi(token, pin, symbol)
    }

    suspend fun getStudentsScrapper(email: String, password: String, endpoint: String, symbol: String): List<StudentAndSemesters> {
        return remote.getStudentsScrapper(email, password, endpoint, symbol)
    }

    suspend fun getStudentsHybrid(email: String, password: String, endpoint: String, symbol: String): List<StudentAndSemesters> {
        return remote.getStudentsHybrid(email, password, endpoint, symbol)
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

    suspend fun saveStudents(students: List<StudentAndSemesters>): List<Long> {
        semestersLocal.saveSemesters(students.flatMap { it.semesters })
        return local.saveStudents(students.map { it.student })
    }

    suspend fun switchStudent(student: StudentAndSemesters) {
        return local.setCurrentStudent(student.student)
    }

    suspend fun logoutStudent(student: Student) {
        return local.logoutStudent(student)
    }
}
