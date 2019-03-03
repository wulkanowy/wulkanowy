package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.repositories.gradessummary.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.reactivex.Completable
import javax.inject.Inject

class GradeSummaryWork @Inject constructor(
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val gradeSummaryRepository: GradeSummaryRepository
) : Work {

    override fun create(): Completable {
        return studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { gradeSummaryRepository.getGradesSummary(it, true) }
            .ignoreElement()
    }
}
