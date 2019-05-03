package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.register.Student
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations

class StudentRemoteTest {

    @Mock
    private lateinit var mockSdk: Sdk

    @Before
    fun initApi() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testRemoteAll() {
        doReturn(Single.just(listOf(Student("", "", 1, "test", "", "", "", 1, Api.LoginType.AUTO))))
            .`when`(mockSdk).getStudents()

        val students = StudentRemote(mockSdk).getStudents("", "", "", "").blockingGet()
        assertEquals(1, students.size)
        assertEquals("test", students.first().studentName)
    }
}
