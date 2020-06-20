package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GradeStatisticsItem
import io.github.wulkanowy.ui.modules.grade.statistics.ViewType
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRepository @Inject constructor(
    private val local: GradeStatisticsLocal,
    private val remote: GradeStatisticsRemote
) {

    fun getGradesStatistics(student: Student, semester: Semester, subjectName: String, isSemester: Boolean, forceRefresh: Boolean = false): Flow<List<GradeStatisticsItem>> {
        return local.getGradesStatistics(semester, isSemester, subjectName).transform {
            if (!forceRefresh && it.isNotEmpty()) return@transform emit(it.mapToStatisticItems())

            val new = remote.getGradeStatistics(student, semester, isSemester)
            val old = local.getGradesStatistics(semester, isSemester).first()

            local.deleteGradesStatistics(old.uniqueSubtract(new))
            local.saveGradesStatistics(new.uniqueSubtract(old))

            emitAll(local.getGradesStatistics(semester, isSemester, subjectName).map { it.mapToStatisticItems() })
        }
    }

    fun getGradesPointsStatistics(student: Student, semester: Semester, subjectName: String, forceRefresh: Boolean): Flow<List<GradeStatisticsItem>> {
        return local.getGradesPointsStatistics(semester, subjectName).transform {
            if (!forceRefresh && it.isNotEmpty()) return@transform emit(it.mapToStatisticsItem())

            val new = remote.getGradePointsStatistics(student, semester)
            val old = local.getGradesPointsStatistics(semester).first()

            local.deleteGradesPointsStatistics(old.uniqueSubtract(new))
            local.saveGradesPointsStatistics(new.uniqueSubtract(old))

            local.getGradesPointsStatistics(semester, subjectName).map { it.mapToStatisticsItem() }
        }
    }

    private fun List<GradeStatistics>.mapToStatisticItems() = groupBy { it.subject }.map {
        GradeStatisticsItem(
            type = ViewType.PARTIAL,
            partial = it.value
                .sortedByDescending { item -> item.grade }
                .filter { item -> item.amount != 0 },
            points = null
        )
    }

    private fun List<GradePointsStatistics>.mapToStatisticsItem() = map {
        GradeStatisticsItem(
            type = ViewType.POINTS,
            partial = emptyList(),
            points = it
        )
    }
}
