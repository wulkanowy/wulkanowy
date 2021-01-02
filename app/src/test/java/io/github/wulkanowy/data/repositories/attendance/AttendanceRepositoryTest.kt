package io.github.wulkanowy.data.repositories.attendance

import io.github.wulkanowy.data.db.dao.AttendanceDao
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.enums.SentExcuseStatus
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.repositories.AttendanceRepository
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.toFirstResult
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDate.of

class AttendanceRepositoryTest {

    @SpyK
    private var mockSdk = Sdk()

    private val semester = Semester(1, 2, "", 1, 3, 2019, now(), now(), 1, 1)

    @MockK
    private lateinit var attendanceDb: AttendanceDao

    private var student = getStudentEntity()

    private lateinit var attendanceRepository: AttendanceRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        attendanceRepository = AttendanceRepository(attendanceDb, mockSdk)
    }

    @Test
    fun saveAndReadTest() {
        val list = listOf(
            getAttendance(of(2018, 9, 10)),
            getAttendance(of(2018, 9, 14)),
            getAttendance(of(2018, 9, 17))
        )

        coEvery { mockSdk.getAttendance(of(2018, 9, 10), of(2018, 9, 17), 1) } returns list
        coEvery { attendanceDb.loadAll(2, 1, of(2018, 9, 10), of(2018, 9, 23)) } returns flowOf(list.mapToEntities(semester))
        coEvery { attendanceDb.insertAll(any()) } returns listOf(1, 2, 3)
        val items = runBlocking {
            attendanceRepository
                .getAttendance(student, semester, of(2018, 9, 10), of(2018, 9, 17), true)
                .toFirstResult()
        }.data

        coVerify { attendanceDb.insertAll(match { it.size == 3 }) }
        coVerify { mockSdk.getAttendance(of(2018, 9, 10), of(2018, 9, 17), 1) }
        assertEquals(3, items?.size)
    }

    private fun getAttendanceEntity(
        date: LocalDate,
        excuseStatus: SentExcuseStatus
    ) = Attendance(
        studentId = 1,
        diaryId = 2,
        timeId = 3,
        date = date,
        number = 0,
        subject = "",
        name = "",
        presence = false,
        absence = false,
        exemption = false,
        lateness = false,
        excused = false,
        deleted = false,
        excusable = false,
        excuseStatus = excuseStatus.name
    )

    @Test
    fun getAttendanceTest() {
        every { mockSdk.init(student) } returns mockSdk
        coEvery {
            mockSdk.getAttendance(
                of(2018, 9, 10),
                of(2018, 9, 15),
                1
            )
        } returns listOf(
            getAttendance(of(2018, 9, 10)),
            getAttendance(of(2018, 9, 17))
        )

        every { semester.studentId } returns 1
        every { semester.diaryId } returns 1
        every { semester.schoolYear } returns 2019
        every { semester.semesterId } returns 1
        every { mockSdk.switchDiary(any(), any()) } returns mockSdk

//        val attendance = runBlocking {
//            AttendanceRemote(mockSdk).getAttendance(student, semesterMock,
//                of(2018, 9, 10),
//                of(2018, 9, 15)
//            )
//        }
//        Assert.assertEquals(2, attendance.size)*/
    }

    private fun getAttendance(date: LocalDate): io.github.wulkanowy.sdk.pojo.Attendance {
        return io.github.wulkanowy.sdk.pojo.Attendance(
            subject = "Fizyka",
            name = "Obecność",
            date = date,
            timeId = 0,
            number = 0,
            deleted = false,
            excusable = false,
            excused = false,
            exemption = false,
            lateness = false,
            presence = false,
            categoryId = 1,
            absence = false,
            excuseStatus = null
        )
    }
}
