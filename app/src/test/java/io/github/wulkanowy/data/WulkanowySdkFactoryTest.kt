package io.github.wulkanowy.data

import android.os.Build
import dagger.hilt.android.testing.HiltTestApplication
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentIsEduOne
import io.github.wulkanowy.getSemesterEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.RegisterStudent
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], application = HiltTestApplication::class)
class WulkanowySdkFactoryTest {

    private lateinit var wulkanowySdkFactory: WulkanowySdkFactory
    private lateinit var studentDao: StudentDao
    private lateinit var semesterDao: SemesterDao
    private lateinit var sdk: Sdk

    @Before
    fun setUp() {
        sdk = mockk(relaxed = true)
        studentDao = mockk()
        semesterDao = mockk()
        wulkanowySdkFactory = spyk(
            WulkanowySdkFactory(
                chuckerInterceptor = mockk(),
                remoteConfig = mockk(relaxed = true),
                webkitCookieManagerProxy = mockk(),
                semesterDb = semesterDao,
                studentDb = studentDao
            )
        )

        every { wulkanowySdkFactory.create() } returns sdk
    }

    @Test
    fun `check sdk flag isEduOne when student is already eduone`() {
        val student = getStudentEntity().copy(isEduOne = true)

        runBlocking {
            wulkanowySdkFactory.create(student)
        }

        verify { sdk.isEduOne = true }
    }

    @Test
    fun `check sdk flag isEduOne when student is not eduone`() {
        val student = getStudentEntity().copy(isEduOne = false)

        runBlocking {
            wulkanowySdkFactory.create(student)
        }

        verify { sdk.isEduOne = false }
    }

    @Test
    fun `check sdk flag isEduOne when student is eduone null and migrating`() {
        val studentToProcess = getStudentEntity().copy(isEduOne = null)
        val registerStudent = studentToProcess.toRegisterStudent(isEduOne = true)
        val semesters = listOf(getSemesterEntity())

        coEvery { studentDao.loadById(any()) } returns studentToProcess
        coEvery { studentDao.update(any(StudentIsEduOne::class)) } just Runs
        coEvery { semesterDao.loadAll(any(), any()) } returns semesters
        coEvery { sdk.getCurrentStudent() } returns registerStudent

        runBlocking {
            wulkanowySdkFactory.create(studentToProcess)
        }

        verify { sdk.isEduOne = true }
    }

    @Test
    fun `check sdk flag isEduOne when student is eduone null and not migrating`() {
        val studentToProcess = getStudentEntity().copy(isEduOne = null)
        val registerStudent = studentToProcess.toRegisterStudent(isEduOne = false)
        val semesters = listOf(getSemesterEntity())

        coEvery { studentDao.loadById(any()) } returns studentToProcess
        coEvery { studentDao.update(any(StudentIsEduOne::class)) } just Runs
        coEvery { semesterDao.loadAll(any(), any()) } returns semesters
        coEvery { sdk.getCurrentStudent() } returns registerStudent

        runBlocking {
            wulkanowySdkFactory.create(studentToProcess)
        }

        verify { sdk.isEduOne = false }
    }

    private fun Student.toRegisterStudent(isEduOne: Boolean) = RegisterStudent(
        studentId = studentId,
        studentName = studentName,
        studentSecondName = studentName,
        studentSurname = studentName,
        className = className,
        classId = classId,
        isParent = isParent,
        isAuthorized = isAuthorized,
        semesters = emptyList(),
        isEduOne = isEduOne,
    )
}
