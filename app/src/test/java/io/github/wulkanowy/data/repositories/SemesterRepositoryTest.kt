package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.TestDispatchersProvider
import io.github.wulkanowy.createSemesterEntity
import io.github.wulkanowy.createSemesterPojo
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate.now

class SemesterRepositoryTest {

    @MockK
    private lateinit var sdk: Sdk

    @MockK
    private lateinit var semesterDb: SemesterDao

    @MockK
    private lateinit var student: Student

    private lateinit var semesterRepository: SemesterRepository

    @Before
    fun initTest() {
        MockKAnnotations.init(this)
        semesterRepository = SemesterRepository(semesterDb, sdk, TestDispatchersProvider())
        every { student.loginMode } returns "SCRAPPER"
    }

    @Test
    fun getSemesters_noSemesters() {
        val semesters = listOf(
            createSemesterPojo(1, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterPojo(1, 2, now().minusMonths(3), now())
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns emptyList()
        coEvery { sdk.getSemesters() } returns semesters
        coEvery { semesterDb.deleteAll(any()) } just Runs
        coEvery { semesterDb.insertSemesters(any()) } returns emptyList()

        runBlocking { semesterRepository.getSemesters(student) }

        coVerify { semesterDb.insertSemesters(semesters.mapToEntities(student.studentId)) }
        coVerify { semesterDb.deleteAll(emptyList()) }
    }

    @Test
    fun getSemesters_invalidDiary_api() {
        every { student.loginMode } returns "API"
        val badSemesters = listOf(
            createSemesterPojo(0, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterPojo(0, 2, now().minusMonths(3), now())
        )

        val goodSemesters = listOf(
            createSemesterPojo(122, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterPojo(123, 2, now().minusMonths(3), now())
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns badSemesters.mapToEntities(student.studentId)
        coEvery { sdk.getSemesters() } returns goodSemesters
        coEvery { semesterDb.deleteAll(any()) } just Runs
        coEvery { semesterDb.insertSemesters(any()) } returns listOf()

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
        assertEquals(0, items[0].diaryId)
    }

    @Test
    fun getSemesters_invalidDiary_scrapper() {
        every { student.loginMode } returns "SCRAPPER"
        val badSemesters = listOf(
            createSemesterPojo(0, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterPojo(0, 2, now().minusMonths(3), now())
        )

        val goodSemesters = listOf(
            createSemesterPojo(1, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterPojo(1, 2, now().minusMonths(3), now())
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returnsMany listOf(
            badSemesters.mapToEntities(student.studentId),
            badSemesters.mapToEntities(student.studentId),
            goodSemesters.mapToEntities(student.studentId)
        )
        coEvery { sdk.getSemesters() } returns goodSemesters
        coEvery { semesterDb.deleteAll(any()) } just Runs
        coEvery { semesterDb.insertSemesters(any()) } returns listOf()

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
        assertNotEquals(0, items[0].diaryId)
    }

    @Test
    fun getSemesters_noCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now().minusMonths(12), now().minusMonths(6)),
            createSemesterEntity(1, 2, now().minusMonths(6), now().minusMonths(1))
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns semesters

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_oneCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(1, 2, now().minusMonths(3), now())
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns semesters

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_doubleCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now(), now()),
            createSemesterEntity(1, 2, now(), now())
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns semesters

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_noSemesters_refreshOnNoCurrent() {
        val semesters = listOf(
            createSemesterPojo(1, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterPojo(1, 2, now().minusMonths(3), now())
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns emptyList()
        coEvery { sdk.getSemesters() } returns semesters
        coEvery { semesterDb.deleteAll(any()) } just Runs
        coEvery { semesterDb.insertSemesters(any()) } returns listOf()

        runBlocking { semesterRepository.getSemesters(student, refreshOnNoCurrent = true) }

        coVerify { semesterDb.deleteAll(emptyList()) }
        coVerify { semesterDb.insertSemesters(semesters.mapToEntities(student.studentId)) }
    }

    @Test
    fun getSemesters_noCurrent_refreshOnNoCurrent() {
        val semestersWithNoCurrent = listOf(
            createSemesterEntity(1, 1, now().minusMonths(12), now().minusMonths(6)),
            createSemesterEntity(1, 2, now().minusMonths(6), now().minusMonths(1))
        )

        val newSemesters = listOf(
            createSemesterPojo(1, 1, now().minusMonths(12), now().minusMonths(6)),
            createSemesterPojo(1, 2, now().minusMonths(6), now().minusMonths(1)),

            createSemesterPojo(2, 1, now().minusMonths(1), now().plusMonths(5)),
            createSemesterPojo(2, 2, now().plusMonths(5), now().plusMonths(11)),
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns semestersWithNoCurrent
        coEvery { sdk.getSemesters() } returns newSemesters
        coEvery { semesterDb.deleteAll(any()) } just Runs
        coEvery { semesterDb.insertSemesters(any()) } returns listOf()

        val items = runBlocking { semesterRepository.getSemesters(student, refreshOnNoCurrent = true) }
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_doubleCurrent_refreshOnNoCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now(), now()),
            createSemesterEntity(1, 2, now(), now())
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns semesters

        val items = runBlocking { semesterRepository.getSemesters(student, refreshOnNoCurrent = true) }
        assertEquals(2, items.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getCurrentSemester_doubleCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now(), now()),
            createSemesterEntity(1, 1, now(), now())
        )

        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns semesters

        runBlocking { semesterRepository.getCurrentSemester(student) }
    }

    @Test(expected = RuntimeException::class)
    fun getCurrentSemester_emptyList() {
        coEvery { semesterDb.loadAll(student.studentId, student.classId) } returns emptyList()

        runBlocking { semesterRepository.getCurrentSemester(student) }
    }
}
