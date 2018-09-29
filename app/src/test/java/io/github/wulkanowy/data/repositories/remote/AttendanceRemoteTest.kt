package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDate
import java.sql.Date

class AttendanceRemoteTest {

    @Mock
    private lateinit var mockApi: Api

    @Mock
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getExamsTest() {
        doReturn(Single.just(listOf(
                getAttendance("2018-09-10"),
                getAttendance("2018-09-17")
        ))).`when`(mockApi).getAttendance(any())

        doReturn("1").`when`(semesterMock).studentId
        doReturn("1").`when`(semesterMock).diaryId

        val attendance = AttendanceRemote(mockApi).getAttendance(semesterMock,
                LocalDate.of(2018, 9, 10)).blockingGet()
        assertEquals(2, attendance.size)
    }

    private fun getAttendance(dateString: String): Attendance {
        return Attendance().apply {
            subject = "Fizyka"
            name = "Obecność"
            date = Date.valueOf(dateString)
        }
    }
}
