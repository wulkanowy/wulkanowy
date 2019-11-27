package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRemote @Inject constructor(private val sdk: SdkHelper) {

    fun getTimetable(semester: Semester, startDate: LocalDate, endDate: LocalDate): Single<List<Timetable>> {
        return sdk.changeSemester(semester).getTimetable(startDate, endDate)
            .map { lessons ->
                lessons.map {
                    Timetable(
                        studentId = semester.studentId,
                        diaryId = semester.diaryId,
                        number = it.number,
                        start = it.start,
                        end = it.end,
                        date = it.date,
                        subject = it.subject,
                        subjectOld = it.subjectOld,
                        group = it.group,
                        room = it.room,
                        roomOld = it.roomOld,
                        teacher = it.teacher,
                        teacherOld = it.teacherOld,
                        info = it.info,
                        changes = it.changes,
                        canceled = it.canceled
                    )
                }
            }
    }
}
