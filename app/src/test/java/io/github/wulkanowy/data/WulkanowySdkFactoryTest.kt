package io.github.wulkanowy.data

import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WulkanowySdkFactoryTest {

    private lateinit var wulkanowySdkFactory: WulkanowySdkFactory
    private lateinit var studentDao: StudentDao
    private lateinit var semesterDao: SemesterDao
    private lateinit var sdk: Sdk

    @Before
    fun setUp() {
        sdk = spyk(Sdk())
        studentDao = mockk()
        semesterDao = mockk()
        wulkanowySdkFactory = spyk(
            WulkanowySdkFactory(
                chuckerInterceptor = mockk(),
                remoteConfig = mockk(relaxed = true),
                webkitCookieManagerProxy = mockk(),
                semesterDb = semesterDao,
                studentDb = studentDao
            )
        )

        every { wulkanowySdkFactory.create() } returns sdk
    }

    @Test
    fun `check sdk flag isEduOne when student is already eduone`() {
        val student = getStudentEntity().copy(isEduOne = true)

        runBlocking {
            wulkanowySdkFactory.create(student)
        }

        assert(sdk.isEduOne)
    }
}
