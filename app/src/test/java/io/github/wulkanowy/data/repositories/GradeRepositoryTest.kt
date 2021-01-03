package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.toFirstResult
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.just
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate.of

class GradeRepositoryTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var gradeDb: GradeDao

    @MockK
    private lateinit var gradeSummaryDb: GradeSummaryDao

    private val semester = getSemester()

    private val student = getStudentEntity()

    private lateinit var gradeRepository: GradeRepository

    @Before
    fun initApi() {
        MockKAnnotations.init(this)

        gradeRepository = GradeRepository(gradeDb, gradeSummaryDb, sdk)
    }

    @Test
    fun markOlderThanRegisterDateAsRead() {
        val remoteList = listOf(
            createGradeApi(5, 4.0, of(2019, 2, 25), "Ocena pojawiła się"),
            createGradeApi(5, 4.0, of(2019, 2, 26), "przed zalogowanie w aplikacji"),
            createGradeApi(5, 4.0, of(2019, 2, 27), "Ocena z dnia logowania"),
            createGradeApi(5, 4.0, of(2019, 2, 28), "Ocena jeszcze nowsza")
        )
        coEvery { sdk.getGrades(1) } returns (remoteList to emptyList())

        coEvery { gradeDb.loadAll(1, 1) } returnsMany listOf(flowOf(listOf()), flowOf(remoteList.mapToEntities(semester)))
        coEvery { gradeDb.deleteAll(any()) } just Runs
        coEvery { gradeDb.insertAll(any()) } returns listOf()

        coEvery { gradeSummaryDb.loadAll(1, 1) } returnsMany listOf(flowOf(listOf()), flowOf(listOf()))
        coEvery { gradeSummaryDb.deleteAll(any()) } just Runs
        coEvery { gradeSummaryDb.insertAll(any()) } returns listOf()

        runBlocking {
            gradeRepository.getGrades(student, semester, true).toFirstResult()
        }

        coVerify {
            gradeDb.insertAll(match { grades ->
                grades[0].isRead &&
                    grades[1].isRead &&
                    !grades[2].isRead &&
                    !grades[3].isRead
            })
        }
    }

    @Test
    fun mitigateOldGradesNotifications() {
        val localList = listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Jedna ocena"),
            createGradeLocal(4, 4.0, of(2019, 2, 26), "Druga"),
            createGradeLocal(3, 5.0, of(2019, 2, 27), "Trzecia")
        )
        coEvery { gradeDb.loadAll(1, 1) } returns flowOf(localList)

        val remoteList = listOf(
            createGradeApi(5, 2.0, of(2019, 2, 25), "Ocena ma datę, jest inna, ale nie zostanie powiadomiona"),
            createGradeApi(4, 3.0, of(2019, 2, 26), "starszą niż ostatnia lokalnie"),
            createGradeApi(3, 4.0, of(2019, 2, 27), "Ta jest z tego samego dnia co ostatnia lokalnie"),
            createGradeApi(2, 5.0, of(2019, 2, 28), "Ta jest już w ogóle nowa")
        )
        coEvery { sdk.getGrades(1) } returns (remoteList to emptyList())

        coEvery { gradeDb.loadAll(1, 1) } returnsMany listOf(flowOf(listOf()), flowOf(remoteList.mapToEntities(semester)))
        coEvery { gradeDb.deleteAll(any()) } just Runs
        coEvery { gradeDb.insertAll(any()) } returns listOf()

        coEvery { gradeSummaryDb.loadAll(1, 1) } returnsMany listOf(flowOf(listOf()), flowOf(listOf()))
        coEvery { gradeSummaryDb.deleteAll(any()) } just Runs
        coEvery { gradeSummaryDb.insertAll(any()) } returns listOf()

        runBlocking {
            gradeRepository.getGrades(student, semester, true).toFirstResult().data!!
        }

        coVerify {
            gradeDb.insertAll(match { grades ->
                grades[0].isRead &&
                    grades[1].isRead &&
                    !grades[2].isRead &&
                    !grades[3].isRead
            })
        }
    }

    @Test
    fun subtractLocaleDuplicateGrades() {
        val localList = listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        coEvery { gradeDb.loadAll(semester.semesterId, student.studentId) } returns flowOf(localList)

        val remoteList = listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        coEvery { sdk.getGrades(semester.semesterId) } returns (remoteList to emptyList())

        coEvery { gradeDb.loadAll(1, 1) } returnsMany listOf(flowOf(listOf()), flowOf(remoteList.mapToEntities(semester)))
        coEvery { gradeDb.deleteAll(any()) } just Runs
        coEvery { gradeDb.insertAll(any()) } returns listOf()

        coEvery { gradeSummaryDb.loadAll(1, 1) } returnsMany listOf(flowOf(listOf()), flowOf(listOf()))
        coEvery { gradeSummaryDb.deleteAll(any()) } just Runs
        coEvery { gradeSummaryDb.insertAll(any()) } returns listOf()

        runBlocking {
            gradeRepository.getGrades(student, semester, true).toFirstResult().data!!
        }

        coVerify { gradeDb.insertAll(match { it.size == 2 }) }
    }

    @Test
    fun subtractRemoteDuplicateGrades() {
        coEvery { gradeDb.loadAll(semester.semesterId, student.studentId) } returns flowOf(listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ))

        coEvery { sdk.getGrades(semester.semesterId) } returns (listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ) to emptyList())

        val grades = runBlocking {
            gradeRepository
                .getGrades(student, semester, true)
                .filter { it.status == Status.SUCCESS }.first().data!!
        }

        assertEquals(3, grades.first.size)
    }

    @Test
    fun emptyLocal() {
        val remoteList = listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        coEvery { sdk.getGrades(1) } returns (remoteList to emptyList())

        coEvery { gradeDb.loadAll(1, 1) } returnsMany listOf(flowOf(listOf()), flowOf(remoteList.mapToEntities(semester)))
        coEvery { gradeDb.deleteAll(any()) } just Runs
        coEvery { gradeDb.insertAll(any()) } returns listOf()

        coEvery { gradeSummaryDb.loadAll(1, 1) } returnsMany listOf(flowOf(listOf()), flowOf(listOf()))
        coEvery { gradeSummaryDb.deleteAll(any()) } just Runs
        coEvery { gradeSummaryDb.insertAll(any()) } returns listOf()

        val grades = runBlocking {
            gradeRepository.getGrades(student, semester, true).toFirstResult().data!!
        }

        assertEquals(3, grades.first.size)
    }

    @Test
    fun emptyRemote() {
        coEvery { gradeDb.loadAll(semester.semesterId, student.studentId) } returns flowOf(listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ))

        coEvery { sdk.getGrades(semester.semesterId) } returns (emptyList<io.github.wulkanowy.sdk.pojo.Grade>() to emptyList())

        val grades = runBlocking {
            gradeRepository
                .getGrades(student, semester, true)
                .filter { it.status == Status.SUCCESS }.first().data!!
        }

        assertEquals(0, grades.first.size)
    }
}
