package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.timetable.TimetableRepository
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.reactivex.Completable
import org.threeten.bp.LocalDate
import javax.inject.Inject

class TimetableWork @Inject constructor(
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val timetableRepository: TimetableRepository
) : Work {

    override fun create(): Completable {
        return studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { timetableRepository.getTimetable(it, LocalDate.now().monday, LocalDate.now().friday, true) }
            .ignoreElement()
    }
}

