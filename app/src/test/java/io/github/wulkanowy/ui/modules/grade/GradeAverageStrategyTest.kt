package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class GradeAverageStrategyTest {

    @Mock
    lateinit var preferencesRepository: PreferencesRepository

    @Mock
    lateinit var gradeRepository: GradeRepository

    private lateinit var gradeAverageStrategy: GradeAverageStrategy

    private val student = Student("", "", "", "", "", 101, "", "", "", "", 1, true, LocalDateTime.now())

    private val semesters = mutableListOf(
        Semester(101, 10, "", 1, 21, 1, false, LocalDate.now(), LocalDate.now(), 1, 1),
        Semester(101, 11, "", 1, 22, 1, false, LocalDate.now(), LocalDate.now(), 1, 1),
        Semester(101, 11, "", 1, 23, 2, true, LocalDate.now(), LocalDate.now(), 1, 1)
    )

    private val firstGrades = listOf(
        Grade(22, 101, "Matematyka", "", 4, .0, "", "", "", "", "", 1.0, LocalDate.now(), ""),
        Grade(22, 101, "Matematyka", "", 3, .0, "", "", "", "", "", 1.0, LocalDate.now(), ""),
        Grade(22, 101, "Fizyka", "", 6, .0, "", "", "", "", "", 1.0, LocalDate.now(), ""),
        Grade(22, 101, "Fizyka", "", 1, .0, "", "", "", "", "", 1.0, LocalDate.now(), "")
    )

    private val secondGrade = listOf(
        Grade(23, 101, "Matematyka", "", 3, .0, "", "", "", "", "", 1.0, LocalDate.now(), ""),
        Grade(23, 101, "Matematyka", "", 2, .0, "", "", "", "", "", 1.0, LocalDate.now(), ""),
        Grade(23, 101, "Fizyka", "", 4, .0, "", "", "", "", "", 1.0, LocalDate.now(), ""),
        Grade(23, 101, "Fizyka", "", 2, .0, "", "", "", "", "", 1.0, LocalDate.now(), "")
    )

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        gradeAverageStrategy = GradeAverageStrategy(preferencesRepository, gradeRepository)

        doReturn(.33).`when`(preferencesRepository).gradeMinusModifier
        doReturn(.33).`when`(preferencesRepository).gradePlusModifier

        doReturn(Single.just(firstGrades)).`when`(gradeRepository).getGrades(student, semesters[1], true)
        doReturn(Single.just(secondGrade)).`when`(gradeRepository).getGrades(student, semesters[2], true)
    }

    @Test
    fun onlyOneSemesterTest() {
        doReturn("only_one_semester").`when`(preferencesRepository).gradeAverageMode

        val averages = gradeAverageStrategy.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(2.5, averages["Matematyka"])
        assertEquals(3.0, averages["Fizyka"])
    }

    @Test
    fun allYearFirstSemesterTest() {
        doReturn("all_year").`when`(preferencesRepository).gradeAverageMode

        val averages = gradeAverageStrategy.getGradeAverage(student, semesters, semesters[1].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.5, averages["Matematyka"])
        assertEquals(3.5, averages["Fizyka"])
    }

    @Test
    fun allYearSecondSemesterTest() {
        doReturn("all_year").`when`(preferencesRepository).gradeAverageMode

        val averages = gradeAverageStrategy.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.0, averages["Matematyka"])
        assertEquals(3.25, averages["Fizyka"])
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectAverageModeTest() {
        doReturn("test_mode").`when`(preferencesRepository).gradeAverageMode

        gradeAverageStrategy.getGradeAverage(student, semesters, semesters[2].semesterId, true).blockingGet()
    }
}
