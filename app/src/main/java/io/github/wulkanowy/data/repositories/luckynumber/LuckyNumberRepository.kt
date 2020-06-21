package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate.now
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRepository @Inject constructor(
    private val local: LuckyNumberLocal,
    private val remote: LuckyNumberRemote
) {

    suspend fun refreshLuckyNumber(student: Student, notify: Boolean = false) {
        val new = remote.getLuckyNumber(student)
        val old = local.getLuckyNumber(student, now()).first()

        if (new != old) {
            old?.let { local.deleteLuckyNumber(it) }
            local.saveLuckyNumber(new?.apply {
                if (notify) isNotified = false
            })
        }
    }

    fun getLuckyNumber(student: Student, notify: Boolean = false): Flow<LuckyNumber?> {
        return local.getLuckyNumber(student, now()).map {
            if (it != null) return@map it
            refreshLuckyNumber(student, notify)
            it
        }
    }

    fun getNotNotifiedLuckyNumber(student: Student): Flow<LuckyNumber?> {
        return local.getLuckyNumber(student, now())
    }

    suspend fun updateLuckyNumber(luckyNumber: LuckyNumber?) {
        local.updateLuckyNumber(luckyNumber)
    }
}
