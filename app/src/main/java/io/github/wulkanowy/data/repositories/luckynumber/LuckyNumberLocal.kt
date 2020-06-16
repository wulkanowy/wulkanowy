package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.db.dao.LuckyNumberDao
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberLocal @Inject constructor(private val luckyNumberDb: LuckyNumberDao) {

    suspend fun saveLuckyNumber(luckyNumber: LuckyNumber) {
        luckyNumberDb.insertAll(listOf(luckyNumber))
    }

    suspend fun updateLuckyNumber(luckyNumber: LuckyNumber) {
        luckyNumberDb.updateAll(listOf(luckyNumber))
    }

    suspend fun deleteLuckyNumber(luckyNumber: LuckyNumber) {
        luckyNumberDb.deleteAll(listOf(luckyNumber))
    }

    suspend fun getLuckyNumber(student: Student, date: LocalDate): LuckyNumber? {
        return luckyNumberDb.load(student.studentId, date)
    }
}
