package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.repositories.homework.HomeworkRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.reactivex.Completable
import org.threeten.bp.LocalDate
import javax.inject.Inject

class HomeworkWork @Inject constructor(
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val homeworkRepository: HomeworkRepository
) : Work {

    override fun create(): Completable {
        return studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { homeworkRepository.getHomework(it, LocalDate.now(), true) }
            .ignoreElement()
    }
}
