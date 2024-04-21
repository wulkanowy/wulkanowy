package io.github.wulkanowy.data.db.dao

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltTestApplication
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.getSemesterEntity
import io.github.wulkanowy.getStudentEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], application = HiltTestApplication::class)
class StudentDaoTest {

    private lateinit var studentDao: StudentDao
    private lateinit var semesterDao: SemesterDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context = context,
            klass = AppDatabase::class.java
        ).build()
        studentDao = db.studentDao
        semesterDao = db.semesterDao
    }

    @Test
    fun `get students associated with correct semester with same studentId`() = runTest {
        val notEduOneStudent = getStudentEntity()
            .copy(
                isEduOne = false,
                classId = 42,
                studentId = 100
            )
            .apply { id = 1 }
        val eduOneStudent = getStudentEntity()
            .copy(
                isEduOne = true,
                classId = 0,
                studentId = 100
            )
            .apply { id = 2 }

        val semesterAssociatedWithNotEduOneStudent = getSemesterEntity()
            .copy(
                studentId = notEduOneStudent.studentId,
                classId = notEduOneStudent.classId,
                diaryId = 1 // make semester unique
            )
            .apply { id = 0 }
        val semesterAssociatedWithEduOneStudent = getSemesterEntity()
            .copy(
                studentId = eduOneStudent.studentId,
                classId = eduOneStudent.classId,
                diaryId = 2 // make semester unique
            )
            .apply { id = 0 }

        studentDao.insertAll(listOf(notEduOneStudent, eduOneStudent))
        semesterDao.insertAll(
            listOf(
                semesterAssociatedWithNotEduOneStudent,
                semesterAssociatedWithEduOneStudent
            )
        )

        val studentsWithSemesters = studentDao.loadStudentsWithSemesters()
        val notEduOneSemestersResult = studentsWithSemesters.entries
            .find { (student, _) -> student.id == notEduOneStudent.id }
            ?.value
        val eduOneSemestersResult = studentsWithSemesters.entries
            .find { (student, _) -> student.id == eduOneStudent.id }
            ?.value

        assertEquals(2, studentsWithSemesters.size)

        assertEquals(1, notEduOneSemestersResult?.size)
        assertEquals(1, eduOneSemestersResult?.size)

        assertEquals(semesterAssociatedWithEduOneStudent, eduOneSemestersResult?.firstOrNull())
        assertEquals(
            semesterAssociatedWithNotEduOneStudent,
            notEduOneSemestersResult?.firstOrNull()
        )
    }

    @Test
    fun `get students associated with correct semester with different studentId`() = runTest {
        val notEduOneStudent = getStudentEntity()
            .copy(
                isEduOne = false,
                classId = 42,
                studentId = 100
            )
            .apply { id = 1 }
        val eduOneStudent = getStudentEntity()
            .copy(
                isEduOne = true,
                classId = 0,
                studentId = 101
            )
            .apply { id = 2 }

        val semesterAssociatedWithNotEduOneStudent = getSemesterEntity()
            .copy(
                studentId = notEduOneStudent.studentId,
                classId = notEduOneStudent.classId,
            )
            .apply { id = 0 }
        val semesterAssociatedWithEduOneStudent = getSemesterEntity()
            .copy(
                studentId = eduOneStudent.studentId,
                classId = eduOneStudent.classId,
            )
            .apply { id = 0 }

        studentDao.insertAll(listOf(notEduOneStudent, eduOneStudent))
        semesterDao.insertAll(
            listOf(
                semesterAssociatedWithNotEduOneStudent,
                semesterAssociatedWithEduOneStudent
            )
        )

        val studentsWithSemesters = studentDao.loadStudentsWithSemesters()
        val notEduOneSemestersResult = studentsWithSemesters.entries
            .find { (student, _) -> student.id == notEduOneStudent.id }
            ?.value
        val eduOneSemestersResult = studentsWithSemesters.entries
            .find { (student, _) -> student.id == eduOneStudent.id }
            ?.value

        assertEquals(2, studentsWithSemesters.size)

        assertEquals(1, notEduOneSemestersResult?.size)
        assertEquals(1, eduOneSemestersResult?.size)

        assertEquals(semesterAssociatedWithEduOneStudent, eduOneSemestersResult?.firstOrNull())
        assertEquals(
            semesterAssociatedWithNotEduOneStudent,
            notEduOneSemestersResult?.firstOrNull()
        )
    }

    @After
    fun closeDb() {
        db.close()
    }
}
