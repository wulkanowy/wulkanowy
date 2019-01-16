package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRemote @Inject constructor(private val api: Api) {

    fun getLuckyNumbers(semester: Semester): Single<List<LuckyNumber>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { it.getLuckyNumber() }
            .map { luckyNumber ->
                if (luckyNumber != 0) {
                    listOf(
                        LuckyNumber(
                            studentId = semester.studentId,
                            date = LocalDate.now(),
                            luckyNumber = luckyNumber
                        )
                    )
                } else emptyList()
            }
    }
}
