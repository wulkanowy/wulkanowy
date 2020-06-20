package io.github.wulkanowy.data.repositories.grade

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRepository @Inject constructor(
    private val local: GradeLocal,
    private val remote: GradeRemote
) {

    suspend fun refreshGrades(student: Student, semester: Semester, notify: Boolean = false) {
        val (newDetails, newSummary) = remote.getGrades(student, semester)

        refreshGradeDetails(student, semester, newDetails, notify)
        refreshGradeSummaries(semester, newSummary, notify)
    }

    private suspend fun refreshGradeDetails(student: Student, semester: Semester, newDetails: List<Grade>, notify: Boolean) {
        val oldGrades = local.getGradesDetails(semester).first()

        val notifyBreakDate = oldGrades.maxBy { it.date }?.date ?: student.registrationDate.toLocalDate()
        local.deleteGrades(oldGrades uniqueSubtract newDetails)
        local.saveGrades((newDetails uniqueSubtract oldGrades).onEach {
            if (it.date >= notifyBreakDate) it.apply {
                isRead = false
                if (notify) isNotified = false
            }
        })
    }

    private suspend fun refreshGradeSummaries(semester: Semester, newSummary: List<GradeSummary>, notify: Boolean) {
        val oldSummaries = local.getGradesSummary(semester).first()

        local.deleteGradesSummary(oldSummaries uniqueSubtract newSummary)
        local.saveGradesSummary((newSummary uniqueSubtract oldSummaries).onEach { summary ->
            val oldSummary = oldSummaries.find { oldSummary -> oldSummary.subject == summary.subject }
            summary.isPredictedGradeNotified = when {
                summary.predictedGrade.isEmpty() -> true
                notify && oldSummary?.predictedGrade != summary.predictedGrade -> false
                else -> true
            }
            summary.isFinalGradeNotified = when {
                summary.finalGrade.isEmpty() -> true
                notify && oldSummary?.finalGrade != summary.finalGrade -> false
                else -> true
            }

            summary.predictedGradeLastChange = when {
                oldSummary == null -> LocalDateTime.now()
                summary.predictedGrade != oldSummary.predictedGrade -> LocalDateTime.now()
                else -> oldSummary.predictedGradeLastChange
            }
            summary.finalGradeLastChange = when {
                oldSummary == null -> LocalDateTime.now()
                summary.finalGrade != oldSummary.finalGrade -> LocalDateTime.now()
                else -> oldSummary.finalGradeLastChange
            }
        })
    }

    fun getGrades(student: Student, semester: Semester): Flow<Pair<List<Grade>, List<GradeSummary>>> {
        return local.getGradesDetails(semester).combine(local.getGradesSummary(semester)) { details, summaries ->
            details to summaries
        }.map { (details, summaries) ->
            if (details.isNotEmpty() || summaries.isNotEmpty()) {
                return@map details to summaries
            }
            refreshGrades(student, semester)

            details to summaries
        }
    }

    fun getUnreadGrades(semester: Semester): Flow<List<Grade>> {
        return local.getGradesDetails(semester).map { it.filter { grade -> !grade.isRead } }
    }

    fun getNotNotifiedGrades(semester: Semester): Flow<List<Grade>> {
        return local.getGradesDetails(semester).map { it.filter { grade -> !grade.isNotified } }
    }

    fun getNotNotifiedPredictedGrades(semester: Semester): Flow<List<GradeSummary>> {
        return local.getGradesSummary(semester).map { it.filter { gradeSummary -> !gradeSummary.isPredictedGradeNotified } }
    }

    fun getNotNotifiedFinalGrades(semester: Semester): Flow<List<GradeSummary>> {
        return local.getGradesSummary(semester).map { it.filter { gradeSummary -> !gradeSummary.isFinalGradeNotified } }
    }

    suspend fun updateGrade(grade: Grade) {
        return local.updateGrades(listOf(grade))
    }

    suspend fun updateGrades(grades: List<Grade>) {
        return local.updateGrades(grades)
    }

    suspend fun updateGradesSummary(gradesSummary: List<GradeSummary>) {
        return local.updateGradesSummary(gradesSummary)
    }
}
