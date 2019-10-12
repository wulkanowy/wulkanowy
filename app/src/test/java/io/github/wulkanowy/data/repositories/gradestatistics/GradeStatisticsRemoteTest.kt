package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatistics
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GradeStatisticsRemoteTest {

    @SpyK
    private var mockSdk = Sdk()

    @MockK
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getGradeStatisticsTest() {
        every { mockSdk.getGradesPartialStatistics(1) } returns Single.just(listOf(
            getGradeStatistics("Fizyka"),
            getGradeStatistics("Matematyka")
        ))

        every { semesterMock.studentId } returns 1
        every { semesterMock.semesterId } returns 1
        every { semesterMock.diaryId } returns 1

        val stats = GradeStatisticsRemote(mockSdk).getGradeStatistics(semesterMock, false).blockingGet()
        assertEquals(2, stats.size)
    }

    @Test
    fun getGradePointsStatisticsTest() {
        every { mockSdk.getGradesPointsStatistics(1) } returns Single.just(listOf(
            getGradePointsStatistics("Fizyka"),
            getGradePointsStatistics("Matematyka")
        ))

        every { mockSdk.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.semesterId } returns 1
        every { semesterMock.semesterName } returns 2
        every { semesterMock.diaryId } returns 1

        val stats = GradeStatisticsRemote(mockSdk).getGradePointsStatistics(semesterMock).blockingGet()
        assertEquals(2, stats.size)
    }

    private fun getGradeStatistics(subjectName: String): GradeStatistics {
        return GradeStatistics(
            subject = subjectName,
            gradeValue = 5,
            amount = 10,
            grade = "",
            semesterId = 1
        )
    }

    private fun getGradePointsStatistics(subjectName: String): GradePointsStatistics {
        return GradePointsStatistics(
            semesterId = 1,
            subject = subjectName,
            student = 0.80,
            others = 0.40
        )
    }
}
