package io.github.wulkanowy.data.repositories.luckynumber

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: LuckyNumberLocal,
    private val remote: LuckyNumberRemote
) {

    suspend fun getLuckyNumber(student: Student, forceRefresh: Boolean = false, notify: Boolean = false): LuckyNumber {
        return local.getLuckyNumber(student, LocalDate.now()).takeIf { it.luckyNumber != -1 && !forceRefresh } ?: run {
            val new = remote.getLuckyNumber(student)

            val old = local.getLuckyNumber(student, LocalDate.now())
            if (new != old) {
                local.deleteLuckyNumber(old)
                local.saveLuckyNumber(new.apply {
                    if (notify) isNotified = false
                })
            }

            local.saveLuckyNumber(new.apply {
                if (notify) isNotified = false
            })

            return local.getLuckyNumber(student, LocalDate.now())
        }
    }

    suspend fun getNotNotifiedLuckyNumber(student: Student): LuckyNumber {
        return local.getLuckyNumber(student, LocalDate.now())
    }

    suspend fun updateLuckyNumber(luckyNumber: LuckyNumber) {
        return local.updateLuckyNumber(luckyNumber)
    }
}
