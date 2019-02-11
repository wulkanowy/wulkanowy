package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.toLocalDate
import io.github.wulkanowy.data.db.entities.Realized
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealizedRemote @Inject constructor(private val api: Api) {

    fun getRealized(semester: Semester, startDate: LocalDate, endDate: LocalDate): Single<List<Realized>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { it.getRealized(startDate, endDate) }
            .map { realized ->
                realized.map {
                    it.absence
                    Realized(
                        studentId = semester.studentId,
                        diaryId = semester.diaryId,
                        date = it.date.toLocalDate(),
                        number = it.number,
                        subject = it.subject,
                        topic = it.topic,
                        teacher = it.teacher,
                        teacherSymbol = it.teacherSymbol,
                        substitution = it.substitution,
                        absence = it.absence,
                        resources = it.resources
                    )
                }
            }
    }
}
