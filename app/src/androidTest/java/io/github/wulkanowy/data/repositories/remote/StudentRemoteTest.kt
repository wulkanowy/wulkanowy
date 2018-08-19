package io.github.wulkanowy.data.repositories.remote

import android.support.test.runner.AndroidJUnit4
import io.github.wulkanowy.api.StudentAndParent
import io.github.wulkanowy.api.Vulcan
import io.github.wulkanowy.api.generic.School
import io.github.wulkanowy.api.login.AccountPermissionException
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import io.github.wulkanowy.api.generic.Student as StudentApi

@RunWith(AndroidJUnit4::class)
class StudentRemoteTest {

    @Test
    fun testRemoteAll() {
        val mockApi = mock(Vulcan::class.java)
        `when`(mockApi.symbols).thenReturn(mutableListOf("przeworsk", "jaroslaw", "zarzecze"))
        `when`(mockApi.schools).thenReturn(mutableListOf(
                School("ZSTIO", "123", false),
                School("ZSZ", "998", true)))

        val mockSnP = mock(StudentAndParent::class.java)
        `when`(mockSnP.students).thenReturn(mutableListOf(
                StudentApi().apply {
                    id = "20"
                    name = "Włodzimierz"
                    isCurrent = false
                }))

        `when`(mockApi.studentAndParent).thenReturn(mockSnP)
        doNothing().`when`(mockApi).setCredentials(any(), any(), any(), any(), any(), any())

        val students = StudentRemote(mockApi).getConnectedStudents("test@test.com", "test123", "test").blockingGet()
        assert(students.size == 6)
        assert(students[3].studentName == "Włodzimierz")

    }

    @Test
    fun testOneEmptySymbol() {
        val mockApi = mock(Vulcan::class.java)
        doNothing().`when`(mockApi).setCredentials(any(), any(), any(), any(), any(), any())
        doReturn(mutableListOf("przeworsk")).`when`(mockApi).symbols
        doThrow(AccountPermissionException::class.java).`when`(mockApi).schools

        val students = StudentRemote(mockApi).getConnectedStudents("test@test.com", "test123", "test").blockingGet()
        assert(students.isEmpty())
    }
}