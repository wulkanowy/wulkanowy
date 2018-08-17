package io.github.wulkanowy.data

class StudentRepositoryTest {

    /*@Test
    fun testConnectedStudentsEmptySingle() {
        val mockRemote = mock(StudentRemote::class.java)
        doReturn(Single.just<List<Student>>(listOf()))
                .`when`(mockRemote).getConnectedStudents(any(), any())

        val settings = InternetObservingSettings.host("127.0.0.1").build()
        val studentRepository = StudentRepository(mock(StudentLocal::class.java), mockRemote, settings)

        val result = studentRepository.getConnectedStudents("test", "test")

        assert(result.blockingGet().isEmpty())
    }*/
}