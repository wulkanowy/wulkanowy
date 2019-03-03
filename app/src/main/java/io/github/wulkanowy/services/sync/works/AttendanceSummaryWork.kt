package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.repositories.attendancesummary.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.reactivex.Completable
import javax.inject.Inject

class AttendanceSummaryWork @Inject constructor(
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val attendanceSummaryRepository: AttendanceSummaryRepository
) : Work {

    override fun create(): Completable {
        return studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { attendanceSummaryRepository.getAttendanceSummary(it, -1, true) }
            .ignoreElement()
    }
}

