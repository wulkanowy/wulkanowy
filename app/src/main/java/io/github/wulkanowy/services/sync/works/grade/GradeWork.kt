package io.github.wulkanowy.services.sync.works.grade

import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.works.Work
import io.reactivex.Completable
import javax.inject.Inject

class GradeWork @Inject constructor(
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val gradeRepository: GradeRepository
) : Work {

    override fun create(): Completable {
        return studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it).map { semester -> it to semester } }
            .flatMap { gradeRepository.getGrades(it.first, it.second, true) }
            .ignoreElement()
    }
}

