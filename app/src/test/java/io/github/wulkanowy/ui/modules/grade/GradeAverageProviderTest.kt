package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.createSemesterEntity
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.of
import org.threeten.bp.LocalDateTime

class GradeAverageProviderTest {

    @Mock
    lateinit var preferencesRepository: PreferencesRepository

    @Mock
    lateinit var semesterRepository: SemesterRepository

    @Mock
    lateinit var gradeRepository: GradeRepository

    private lateinit var gradeAverageProvider: GradeAverageProvider

    private val student = Student("", "", "", "SCRAPPER", "", "", false, "", "", "", 101, 0, "", "", "", "", "", 1, true, LocalDateTime.now())

    private val semesters = mutableListOf(
        createSemesterEntity(10, 21, of(2019, 1, 31), of(2019, 6, 23)),
        createSemesterEntity(11, 22, of(2019, 9, 1), of(2020, 1, 31)),
        createSemesterEntity(11, 23, of(2020, 2, 1), now(), semesterName = 2)
    )

    private val firstGrades = listOf(
        getGrade(22, "Matematyka", 4.0),
        getGrade(22, "Matematyka", 3.0),
        getGrade(22, "Fizyka", 6.0),
        getGrade(22, "Fizyka", 1.0)
    )

    private val firstSummaries = listOf(
        getSummary(semesterId = 22, subject = "Matematyka", average = .0),
        getSummary(semesterId = 22, subject = "Fizyka", average = .0)
    )

    private val secondGrades = listOf(
        getGrade(23, "Matematyka", 2.0),
        getGrade(23, "Matematyka", 3.0),
        getGrade(23, "Fizyka", 4.0),
        getGrade(23, "Fizyka", 2.0)
    )

    private val secondSummaries = listOf(
        getSummary(semesterId = 23, subject = "Matematyka", average = .0),
        getSummary(semesterId = 23, subject = "Fizyka", average = .0)
    )

    private val secondGradeWithModifier = listOf(
        getGrade(24, "Język polski", 3.0, -0.50),
        getGrade(24, "Język polski", 4.0, 0.25)
    )

    private val secondSummariesWithModifier = listOf(
        getSummary(24, "Język polski", .0)
    )

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        gradeAverageProvider = GradeAverageProvider(semesterRepository, gradeRepository, preferencesRepository)

        doReturn(.33).`when`(preferencesRepository).gradeMinusModifier
        doReturn(.33).`when`(preferencesRepository).gradePlusModifier
        doReturn(false).`when`(preferencesRepository).gradeAverageForceCalc
    }

    @Test
    fun onlyOneSemesterTest() {
        `when`(preferencesRepository.gradeAverageMode).thenReturn("only_one_semester")
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))
        `when`(gradeRepository.getGrades(student, semesters[2], true)).thenReturn(Single.just(Pair(secondGrades, secondSummaries)))

        val items = gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[2].semesterId, true).blockingGet()

        assertEquals(2, items.size)
        assertEquals(2.5, items.single { it.subject == "Matematyka" }.average, .0)
        assertEquals(3.0, items.single { it.subject == "Fizyka" }.average, .0)
    }

    @Test
    fun onlyOneSemester_gradesWithModifiers_default() {
        `when`(preferencesRepository.gradeAverageMode).thenReturn("only_one_semester")
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))
        `when`(gradeRepository.getGrades(student, semesters[2], true)).thenReturn(Single.just(Pair(secondGradeWithModifier, secondSummariesWithModifier)))

        val averages = gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[2].semesterId, true).blockingGet()

        assertEquals(3.5, averages.single { it.subject == "Język polski" }.average, .0)
    }

    @Test
    fun onlyOneSemester_gradesWithModifiers_api() {
        val student = student.copy(loginMode = Sdk.Mode.API.name)

        `when`(preferencesRepository.gradeAverageMode).thenReturn("only_one_semester")
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))
        `when`(gradeRepository.getGrades(student, semesters[2], true)).thenReturn(Single.just(Pair(secondGradeWithModifier, secondSummariesWithModifier)))

        val averages = gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[2].semesterId, true).blockingGet()

        assertEquals(3.375, averages.single { it.subject == "Język polski" }.average, .0)
    }

    @Test
    fun onlyOneSemester_gradesWithModifiers_scrapper() {
        val student = student.copy(loginMode = Sdk.Mode.SCRAPPER.name)

        `when`(preferencesRepository.gradeAverageMode).thenReturn("only_one_semester")
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))
        `when`(gradeRepository.getGrades(student, semesters[2], true)).thenReturn(Single.just(Pair(secondGradeWithModifier, secondSummariesWithModifier)))

        val averages = gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[2].semesterId, true).blockingGet()

        assertEquals(3.5, averages.single { it.subject == "Język polski" }.average, .0)
    }

    @Test
    fun onlyOneSemester_gradesWithModifiers_hybrid() {
        val student = student.copy(loginMode = Sdk.Mode.HYBRID.name)

        `when`(preferencesRepository.gradeAverageMode).thenReturn("only_one_semester")
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))
        `when`(gradeRepository.getGrades(student, semesters[2], true)).thenReturn(Single.just(Pair(secondGradeWithModifier, secondSummariesWithModifier)))

        val averages = gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[2].semesterId, true).blockingGet()

        assertEquals(3.375, averages.single { it.subject == "Język polski" }.average, .0)
    }

    @Test
    fun allYearFirstSemesterTest() {
        `when`(preferencesRepository.gradeAverageMode).thenReturn("all_year")
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))
        `when`(gradeRepository.getGrades(student, semesters[1], true)).thenReturn(Single.just(Pair(firstGrades, firstSummaries)))

        val averages = gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[1].semesterId, true).blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.5, averages.single { it.subject == "Matematyka" }.average, .0)
        assertEquals(3.5, averages.single { it.subject == "Fizyka" }.average, .0)
    }

    @Test
    fun allYearSecondSemesterTest() {
        `when`(preferencesRepository.gradeAverageMode).thenReturn("all_year")
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))
        `when`(gradeRepository.getGrades(student, semesters[1], true)).thenReturn(Single.just(Pair(firstGrades, firstSummaries)))
        `when`(gradeRepository.getGrades(student, semesters[2], true)).thenReturn(Single.just(Pair(secondGrades, secondSummaries)))

        val averages = gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[2].semesterId, true).blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.0, averages.single { it.subject == "Matematyka" }.average, .0)
        assertEquals(3.25, averages.single { it.subject == "Fizyka" }.average, .0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectAverageModeTest() {
        `when`(preferencesRepository.gradeAverageMode).thenReturn("test_mode")
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))

        gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[2].semesterId, true).blockingGet()
    }

    @Test
    fun allYearSemester_averageFromSummary() {
        `when`(preferencesRepository.gradeAverageMode).thenReturn("all_year")
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))
        `when`(gradeRepository.getGrades(student, semesters[1], true)).thenReturn(Single.just(Pair(firstGrades, emptyList())))
        `when`(gradeRepository.getGrades(student, semesters[2], true)).thenReturn(Single.just(Pair(secondGrades, listOf(
            getSummary(22, "Matematyka", 3.1),
            getSummary(22, "Fizyka", 3.26)
        ))))

        val averages = gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[2].semesterId, true).blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.1, averages.single { it.subject == "Matematyka" }.average, .0)
        assertEquals(3.26, averages.single { it.subject == "Fizyka" }.average, .0)
    }

    @Test
    fun onlyOneSemester_averageFromSummary_forceCalc() {
        `when`(preferencesRepository.gradeAverageMode).thenReturn("all_year")
        `when`(preferencesRepository.gradeAverageForceCalc).thenReturn(true)
        `when`(semesterRepository.getSemesters(student)).thenReturn(Single.just(semesters))
        `when`(gradeRepository.getGrades(student, semesters[1], true)).thenReturn(Single.just(Pair(firstGrades, firstSummaries)))
        `when`(gradeRepository.getGrades(student, semesters[2], true)).thenReturn(Single.just(Pair(secondGrades, listOf(
            getSummary(22, "Matematyka", 1.1),
            getSummary(22, "Fizyka", 7.26)
        ))))

        val averages = gradeAverageProvider.getGradesDetailsWithAverage(student, semesters[2].semesterId, true).blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.0, averages.single { it.subject == "Matematyka" }.average, .0)
        assertEquals(3.25, averages.single { it.subject == "Fizyka" }.average, .0)
    }

    private fun getGrade(semesterId: Int, subject: String, value: Double, modifier: Double = 0.0): Grade {
        return Grade(
            studentId = 101,
            semesterId = semesterId,
            subject = subject,
            value = value,
            modifier = modifier,
            weightValue = 1.0,
            teacher = "",
            date = now(),
            weight = "",
            gradeSymbol = "",
            entry = "",
            description = "",
            comment = "",
            color = ""
        )
    }

    private fun getSummary(semesterId: Int, subject: String, average: Double): GradeSummary {
        return GradeSummary(
            studentId = 101,
            semesterId = semesterId,
            subject = subject,
            average = average,
            pointsSum = "",
            proposedPoints = "",
            finalPoints = "",
            finalGrade = "",
            predictedGrade = "",
            position = 0
        )
    }
}
