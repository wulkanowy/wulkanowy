package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.timetable.Timetable
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDate
import java.sql.Date

class TimetableRemoteTest {

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
                getTimetable("2018-09-10"),
                getTimetable("2018-09-17")
        ))).`when`(mockApi).getTimetable(ArgumentMatchers.any())

        doReturn("1").`when`(semesterMock).studentId
        doReturn("1").`when`(semesterMock).diaryId

        val exams = TimetableRemote(mockApi).getLessons(semesterMock, LocalDate.of(2018, 9, 10)).blockingGet()
        assertEquals(2, exams.size)
    }

    private fun getTimetable(dateString: String): Timetable {
        return Timetable(date = Date.valueOf(dateString))
    }
}
