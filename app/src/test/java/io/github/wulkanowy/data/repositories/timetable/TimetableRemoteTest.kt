package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Timetable
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.of
import org.threeten.bp.LocalDateTime.now

class TimetableRemoteTest {

    @MockK
    private lateinit var mockSdk: Sdk

    private lateinit var mockHelper: SdkHelper

    @MockK
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
        mockHelper = SdkHelper(mockSdk)
    }

    @Test
    fun getTimetableTest() {
        every {
            mockSdk.getTimetable(
                of(2018, 9, 10),
                of(2018, 9, 15)
            )
        } returns Single.just(listOf(
            getTimetable(of(2018, 9, 10)),
            getTimetable(of(2018, 9, 17))
        ))

        every { mockSdk.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1
        every { semesterMock.schoolYear } returns 2019
        every { semesterMock.semesterId } returns 1
        every { mockSdk setProperty "schoolYear" value 2019 } just runs
        every { mockSdk setProperty "diaryId" value 1 } just runs

        val timetable = TimetableRemote(mockHelper).getTimetable(semesterMock,
            of(2018, 9, 10),
            of(2018, 9, 15)
        ).blockingGet()
        assertEquals(2, timetable.size)
    }

    private fun getTimetable(date: LocalDate): Timetable {
        return Timetable(
            date = date,
            number = 0,
            teacherOld = "",
            subjectOld = "",
            roomOld = "",
            subject = "",
            teacher = "",
            group = "",
            canceled = false,
            changes = false,
            info = "",
            room = "",
            end = now(),
            start = now(),
            studentPlan = true
        )
    }
}
