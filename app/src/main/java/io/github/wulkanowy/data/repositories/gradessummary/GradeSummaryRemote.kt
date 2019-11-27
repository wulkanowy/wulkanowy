package io.github.wulkanowy.data.repositories.gradessummary

import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeSummaryRemote @Inject constructor(private val sdk: SdkHelper) {

    fun getGradeSummary(semester: Semester): Single<List<GradeSummary>> {
        return sdk.changeSemester(semester).getGradesSummary(semester.semesterId)
            .map { gradesSummary ->
                gradesSummary.map {
                    GradeSummary(
                        semesterId = semester.semesterId,
                        studentId = semester.studentId,
                        position = 0,
                        subject = it.name,
                        predictedGrade = it.predicted,
                        finalGrade = it.final,
                        pointsSum = it.pointsSum,
                        proposedPoints = it.proposedPoints,
                        finalPoints = it.finalPoints,
                        average = it.average
                    )
                }
            }
    }
}
