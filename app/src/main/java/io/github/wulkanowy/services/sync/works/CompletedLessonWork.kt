package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.repositories.completedlessons.CompletedLessonsRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.reactivex.Completable
import org.threeten.bp.LocalDate
import javax.inject.Inject

class CompletedLessonWork @Inject constructor(
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val completedLessonsRepository: CompletedLessonsRepository
) : Work {

    override fun create(): Completable {
        return studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { completedLessonsRepository.getCompletedLessons(it, LocalDate.now().monday, LocalDate.now().friday, true) }
            .ignoreElement()
    }
}

