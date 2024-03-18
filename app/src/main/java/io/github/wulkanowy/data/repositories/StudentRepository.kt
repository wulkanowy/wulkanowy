package io.github.wulkanowy.data.repositories

import androidx.room.withTransaction
import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentName
import io.github.wulkanowy.data.db.entities.StudentNickAndAvatar
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.mappers.mapToPojo
import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.security.Scrambler
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val studentDb: StudentDao,
    private val semesterDb: SemesterDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
    private val appDatabase: AppDatabase,
    private val scrambler: Scrambler,
    private val passwordRepository: PasswordRepository
) {

    suspend fun isCurrentStudentSet() = studentDb.loadCurrent()?.isCurrent ?: false

    suspend fun getStudentsApi(
        pin: String,
        symbol: String,
        token: String
    ): RegisterUser = wulkanowySdkFactory.create()
        .getStudentsFromHebe(token, pin, symbol, "")
        .mapToPojo(null)

    suspend fun getUserSubjectsFromScrapper(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        domainSuffix: String,
        symbol: String
    ): RegisterUser = wulkanowySdkFactory.create()
        .getUserSubjectsFromScrapper(email, password, scrapperBaseUrl, domainSuffix, symbol)
        .mapToPojo(password)

    suspend fun getStudentsHybrid(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        symbol: String
    ): RegisterUser = wulkanowySdkFactory.create()
        .getStudentsHybrid(email, password, scrapperBaseUrl, "", symbol)
        .mapToPojo(password)

    suspend fun getSavedStudents(): List<StudentWithSemesters> {
        return studentDb.loadStudentsWithSemesters().map { (student, semesters) ->
            StudentWithSemesters(student, semesters)
        }
    }

    suspend fun getSavedStudentById(id: Long): StudentWithSemesters? =
        studentDb.loadStudentWithSemestersById(id).let { res ->
            StudentWithSemesters(
                student = res.keys.firstOrNull() ?: return null,
                semesters = res.values.first(),
            )
        }

    suspend fun getStudentById(id: Long): Student {
        return studentDb.loadById(id) ?: throw NoCurrentStudentException()
    }

    suspend fun getCurrentStudent(): Student {
        return studentDb.loadCurrent() ?: throw NoCurrentStudentException()
    }

    suspend fun saveStudents(studentsWithSemesters: List<StudentWithSemesters>) {
        val semesters = studentsWithSemesters.flatMap { it.semesters }
        val students = studentsWithSemesters.map { it.student }
            .map {
                passwordRepository.savePassword(it)
                it.apply { password = "" }
            }
            .mapIndexed { index, student ->
                if (index == 0) {
                    student.copy(isCurrent = true).apply { avatarColor = student.avatarColor }
                } else student
            }

        appDatabase.withTransaction {
            studentDb.resetCurrent()
            semesterDb.insertSemesters(semesters)
            studentDb.insertAll(students)
        }
    }

    suspend fun switchStudent(studentWithSemesters: StudentWithSemesters) {
        studentDb.switchCurrent(studentWithSemesters.student.id)
    }

    suspend fun logoutStudent(student: Student) = studentDb.delete(student)

    suspend fun updateStudentNickAndAvatar(studentNickAndAvatar: StudentNickAndAvatar) =
        studentDb.update(studentNickAndAvatar)

    suspend fun isOneUniqueStudent() = getSavedStudents()
        .distinctBy { it.student.studentName }.size == 1

    suspend fun authorizePermission(student: Student, semester: Semester, pesel: String) =
        wulkanowySdkFactory.create(student, semester)
            .authorizePermission(pesel)

    suspend fun refreshStudentName(student: Student, semester: Semester) {
        val newCurrentApiStudent = wulkanowySdkFactory.create(student, semester)
            .getCurrentStudent() ?: return

        val studentName = StudentName(
            studentName = "${newCurrentApiStudent.studentName} ${newCurrentApiStudent.studentSurname}"
        ).apply { id = student.id }

        studentDb.update(studentName)
    }

    suspend fun deleteStudentsAssociatedWithAccount(student: Student) {
        studentDb.deleteByEmailAndUserName(student.email, student.userName)
    }

    suspend fun clearAll() {
        withContext(dispatchers.io) {
            scrambler.clearKeyPair()
            appDatabase.clearAllTables()
        }
    }
}
