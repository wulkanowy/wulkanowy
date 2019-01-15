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
class LuckyNumberLocal @Inject constructor(val luckyNumberDb: LuckyNumberDao) {

    fun getLuckyNumbers(semester: Semester, date: LocalDate?): Maybe<List<LuckyNumber>> {
        if (date !== null) {
            return luckyNumberDb.loadFromDate(semester.studentId, date).filter { !it.isEmpty() }
        } else {
            return luckyNumberDb.loadAll(semester.studentId).filter { !it.isEmpty() }
        }
    }

    fun saveLuckyNumbers(luckyNumbers: List<LuckyNumber>) {
        luckyNumberDb.insertAll(luckyNumbers)
    }

    fun updateLuckyNumber(luckyNumber: LuckyNumber): Completable {
        return Completable.fromCallable { luckyNumberDb.update(luckyNumber) }
    }

    fun updateLuckyNumbers(luckyNumbers: List<LuckyNumber>): Completable {
        return Completable.fromCallable { luckyNumberDb.updateAll(luckyNumbers) }
    }

    fun deleteLuckyNumbers(luckyNumbers: List<LuckyNumber>) {
        luckyNumberDb.deleteAll(luckyNumbers)
    }
}
