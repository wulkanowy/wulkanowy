package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Student
import io.github.wulkanowy.utils.AppInfo
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StudentRemoteTest {

    @MockK
    private lateinit var mockSdk: Sdk

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testRemoteAll() {
        coEvery { mockSdk.getStudentsFromScrapper(any(), any(), any(), any()) } returns listOf(
            getStudent("test")
        )

        val appInfo = mockk<AppInfo>()
        every { appInfo.defaultColorsForAvatar } returns listOf(1)

        val students = runBlocking {
            StudentRemote(mockSdk, appInfo).getStudentsScrapper(
                "",
                "",
                "http://fakelog.cf",
                ""
            )
        }
        assertEquals(1, students.size)
        assertEquals("test Kowalski", students.first().student.studentName)
    }

    private fun getStudent(name: String): Student {
        return Student(
            email = "",
            symbol = "",
            studentId = 0,
            userLoginId = 0,
            userLogin = "",
            userName = "",
            studentName = name,
            studentSurname = "Kowalski",
            schoolSymbol = "",
            schoolShortName = "",
            schoolName = "",
            className = "",
            classId = 0,
            certificateKey = "",
            privateKey = "",
            loginMode = Sdk.Mode.SCRAPPER,
            mobileBaseUrl = "",
            loginType = Sdk.ScrapperLoginType.STANDARD,
            scrapperBaseUrl = "",
            isParent = false,
            semesters = emptyList()
        )
    }
}
