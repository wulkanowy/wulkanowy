package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.GradeStatisticsRepository
import io.github.wulkanowy.data.throwOnCaptchaException
import io.github.wulkanowy.data.waitForResult

import javax.inject.Inject

class GradeStatisticsWork @Inject constructor(
    private val gradeStatisticsRepository: GradeStatisticsRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester, notify: Boolean) {
        with(gradeStatisticsRepository) {
            getGradesPartialStatistics(
                student,
                semester,
                "Wszystkie",
                forceRefresh = true
            ).throwOnCaptchaException().waitForResult()
            getGradesSemesterStatistics(
                student,
                semester,
                "Wszystkie",
                forceRefresh = true
            ).throwOnCaptchaException().waitForResult()
            getGradesPointsStatistics(
                student,
                semester,
                "Wszystkie",
                forceRefresh = true
            ).throwOnCaptchaException().waitForResult()
        }
    }
}
