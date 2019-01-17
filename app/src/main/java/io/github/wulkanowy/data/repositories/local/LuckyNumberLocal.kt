package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.LuckyNumberDao
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Completable
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberLocal @Inject constructor(private val luckyNumberDb: LuckyNumberDao) {

    fun getLuckyNumbers(semester: Semester, date: LocalDate): Maybe<List<LuckyNumber>> {
        return luckyNumberDb.loadFromDate(semester.studentId, date).filter { !it.isEmpty() }
    }

    fun saveLuckyNumbers(luckyNumbers: List<LuckyNumber>) {
        luckyNumberDb.insertAll(luckyNumbers)
    }

    fun deleteLuckyNumbers(luckyNumbers: List<LuckyNumber>) {
        luckyNumberDb.deleteAll(luckyNumbers)
    }
}
