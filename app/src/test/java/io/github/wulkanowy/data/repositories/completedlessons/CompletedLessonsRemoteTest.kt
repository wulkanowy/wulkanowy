package io.github.wulkanowy.data.repositories.completedlessons

import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.CompletedLesson
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.just
import io.mockk.runs
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.of

class CompletedLessonsRemoteTest {

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
    fun getCompletedLessonsTest() {
        every {
            mockSdk.getCompletedLessons(
                of(2018, 9, 10),
                of(2018, 9, 15)
            )
        } returns Single.just(listOf(
            getCompletedLesson(of(2018, 9, 10)),
            getCompletedLesson(of(2018, 9, 17))
        ))

        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1
        every { semesterMock.schoolYear } returns 2019
        every { mockSdk setProperty "schoolYear" value 2019 } just runs
        every { mockSdk setProperty "diaryId" value 1 } just runs

        val completed = CompletedLessonsRemote(mockHelper).getCompletedLessons(semesterMock,
            of(2018, 9, 10),
            of(2018, 9, 15)
        ).blockingGet()
        Assert.assertEquals(2, completed.size)
    }

    private fun getCompletedLesson(date: LocalDate): CompletedLesson {
        return CompletedLesson(
            date = date,
            subject = "",
            absence = "",
            resources = "",
            substitution = "",
            teacherSymbol = "",
            teacher = "",
            topic = "",
            number = 1
        )
    }
}
