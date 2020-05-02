package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.changeModifier
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class GradeAverageProvider @Inject constructor(
    private val semesterRepository: SemesterRepository,
    private val gradeRepository: GradeRepository,
    private val preferencesRepository: PreferencesRepository
) {

    private val plusModifier = preferencesRepository.gradePlusModifier

    private val minusModifier = preferencesRepository.gradeMinusModifier

    fun getGradesDetailsWithAverage(student: Student, semesterId: Int, forceRefresh: Boolean): Single<List<GradeDetailsWithAverage>> {
        return semesterRepository.getSemesters(student).flatMap { semesters ->
            when (preferencesRepository.gradeAverageMode) {
                "all_year" -> getAllYearDetailsWithAverage(student, semesters, semesterId, forceRefresh)
                "only_one_semester" -> getSelectedSemesterDetailsWithAverage(student, semesters, semesterId, forceRefresh)
                else -> throw IllegalArgumentException("Incorrect grade average mode: ${preferencesRepository.gradeAverageMode} ")
            }
        }
    }

    private fun getSemesterDetailsWithAverage(student: Student, semester: Semester, forceRefresh: Boolean): Single<List<GradeDetailsWithAverage>> {
        return gradeRepository.getGrades(student, semester, forceRefresh).map { (details, summary) ->
            val allGrades = details.groupBy { it.subject }
            summary.map {
                GradeDetailsWithAverage(
                    subject = it.subject,
                    average = it.average,
                    points = it.pointsSum,
                    grades = allGrades[it.subject].orEmpty()
                )
            }
        }
    }

    private fun getSelectedSemesterDetailsWithAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<List<GradeDetailsWithAverage>> {
        return getSemesterDetailsWithAverage(student, semesters.single { it.semesterId == semesterId }, forceRefresh).map { details ->
            if (!details.any { it.average != .0 } || preferencesRepository.gradeAverageForceCalc) {
                details.map {
                    it.copy(average = it.grades.map { grade ->
                        if (student.loginMode == Sdk.Mode.SCRAPPER.name) {
                            grade.changeModifier(plusModifier, minusModifier)
                        } else grade
                    }.calcAverage())
                }
            } else details
        }
    }

    private fun getAllYearDetailsWithAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<List<GradeDetailsWithAverage>> {
        val selectedSemester = semesters.single { it.semesterId == semesterId }
        val firstSemester = semesters.single { it.diaryId == selectedSemester.diaryId && it.semesterName == 1 }

        return getSemesterDetailsWithAverage(student, selectedSemester, forceRefresh).flatMap { selectedDetails ->
            if (selectedSemester == firstSemester) Single.just(selectedDetails)
            else getSemesterDetailsWithAverage(student, firstSemester, forceRefresh).map { secondDetails ->
                selectedDetails.map { selected ->
                    val second = secondDetails.singleOrNull { it.subject == selected.subject }
                    selected.copy(
                        average = if (!selectedDetails.any { it.average != .0 } || preferencesRepository.gradeAverageForceCalc) {
                            (selected.grades + second?.grades.orEmpty())
                                .map { if (student.loginMode == Sdk.Mode.SCRAPPER.name) it.changeModifier(plusModifier, minusModifier) else it }
                                .calcAverage()
                        } else (selected.average + (second?.average ?: .0)) / 2
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private fun getAllYearAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<List<Triple<String, Double, String>>> {
        val selectedSemester = semesters.single { it.semesterId == semesterId }
        val firstSemester = semesters.single { it.diaryId == selectedSemester.diaryId && it.semesterName == 1 }

        return getAverageFromGradeSummary(student, selectedSemester, forceRefresh)
            .switchIfEmpty(gradeRepository.getGradesDetails(student, selectedSemester, forceRefresh)
                .flatMap { firstGrades ->
                    if (selectedSemester == firstSemester) Single.just(firstGrades)
                    else {
                        gradeRepository.getGradesDetails(student, firstSemester)
                            .map { secondGrades -> secondGrades + firstGrades }
                    }
                }.map { grades ->
                    grades.map { if (student.loginMode == Sdk.Mode.SCRAPPER.name) it.changeModifier(plusModifier, minusModifier) else it }
                        .groupBy { it.subject }
                        .map { Triple(it.key, it.value.calcAverage(), "") }
                })
    }

    private fun getAverageFromGradeSummary(student: Student, selectedSemester: Semester, forceRefresh: Boolean): Maybe<List<Triple<String, Double, String>>> {
        return gradeRepository.getGradesSummary(student, selectedSemester, forceRefresh)
            .toMaybe()
            .flatMap {
                if (it.any { summary -> summary.average != .0 }) {
                    Maybe.just(it.map { summary -> Triple(summary.subject, summary.average, summary.pointsSum) })
                } else Maybe.empty()
            }.filter { !preferencesRepository.gradeAverageForceCalc }
    }

    private fun getOnlyOneSemesterAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<List<Triple<String, Double, String>>> {
        val selectedSemester = semesters.single { it.semesterId == semesterId }

        return getAverageFromGradeSummary(student, selectedSemester, forceRefresh)
            .switchIfEmpty(gradeRepository.getGradesDetails(student, selectedSemester, forceRefresh)
                .map { grades ->
                    grades.map { if (student.loginMode == Sdk.Mode.SCRAPPER.name) it.changeModifier(plusModifier, minusModifier) else it }
                        .groupBy { it.subject }
                        .map { Triple(it.key, it.value.calcAverage(), "") }
                })
    }

    fun getGradeAverage(student: Student, semesters: List<Semester>, selectedSemesterId: Int, forceRefresh: Boolean): Single<List<Triple<String, Double, String>>> {
        return when (preferencesRepository.gradeAverageMode) {
            "all_year" -> getAllYearAverage(student, semesters, selectedSemesterId, forceRefresh)
            "only_one_semester" -> getOnlyOneSemesterAverage(student, semesters, selectedSemesterId, forceRefresh)
            else -> throw IllegalArgumentException("Incorrect grade average mode: ${preferencesRepository.gradeAverageMode} ")
        }
    }
}
