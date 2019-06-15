package io.github.wulkanowy.data.repositories.exam

import io.github.wulkanowy.sdk.pojo.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.of

class ExamRemoteTest {

    @SpyK
    private var mockSdk = Sdk()

    @MockK
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getExamsTest() {
        every {
            mockSdk.getExams(
                of(2018, 9, 10),
                of(2018, 9, 15),
                1
            )
        } returns Single.just(listOf(
            getExam(of(2018, 9, 10)),
            getExam(of(2018, 9, 17))
        ))

        every { mockSdk.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1

        val exams = ExamRemote(mockSdk).getExams(semesterMock,
            of(2018, 9, 10),
            of(2018, 9, 15)
        ).blockingGet()
        assertEquals(2, exams.size)
    }

    private fun getExam(date: LocalDate): Exam {
        return Exam(
            subject = "",
            group = "",
            type = "",
            description = "",
            teacher = "",
            teacherSymbol = "",
            date = date,
            entryDate = date
        )
    }
}
