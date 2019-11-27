package io.github.wulkanowy.data.repositories.homework

import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkRemote @Inject constructor(private val sdk: SdkHelper) {

    fun getHomework(semester: Semester, startDate: LocalDate, endDate: LocalDate): Single<List<Homework>> {
        return sdk.changeSemester(semester).getHomework(startDate, endDate)
            .map { homework ->
                homework.map {
                    Homework(
                        semesterId = semester.semesterId,
                        studentId = semester.studentId,
                        date = it.date,
                        entryDate = it.entryDate,
                        subject = it.subject,
                        content = it.content,
                        teacher = it.teacher,
                        teacherSymbol = it.teacherSymbol
                    )
                }
            }
    }
}
