package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.RealizedDao
import io.github.wulkanowy.data.db.entities.Realized
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealizedLocal @Inject constructor(private val realizedDb: RealizedDao) {

    fun getRealized(semester: Semester, start: LocalDate, end: LocalDate): Maybe<List<Realized>> {
        return realizedDb.loadAll(semester.diaryId, semester.studentId, start, end).filter { !it.isEmpty() }
    }

    fun saveRealized(realized: List<Realized>) {
        realizedDb.insertAll(realized)
    }

    fun deleteExams(realized: List<Realized>) {
        realizedDb.deleteAll(realized)
    }
}
