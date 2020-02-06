package io.github.wulkanowy.data.repositories.exam

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: ExamLocal,
    private val remote: ExamRemote,
    private val preferencesRepository: PreferencesRepository
) {

    fun getExams(semester: Semester, startDate: LocalDate, endDate: LocalDate, forceRefresh: Boolean = false): Single<List<Exam>> {
        return Single.fromCallable { startDate.monday to endDate.friday }
            .flatMap { dates ->
                local.getExams(semester, dates.first, dates.second).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getExams(semester, dates.first, dates.second)
                            else Single.error(UnknownHostException())
                        }.flatMap { new ->
                            local.getExams(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                                .doOnSuccess { old ->
                                    local.deleteExams(old.uniqueSubtract(new))
                                    local.saveExams(new.uniqueSubtract(old))
                                }
                        }.flatMap {
                            local.getExams(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                        }).map { list -> list.filter { it.date in startDate..endDate } }
            }
    }

    fun getNotCSyncedExams(semester: Semester): Single<List<Exam>> {

        return local.getExams(semester, now().monday, now().plusWeeks(4).friday).map { it.filter { exam -> !exam.cSync && preferencesRepository.isCalendarSyncEnable } }.toSingle(emptyList())
    }


    fun updateExams(exams: List<Exam>): Completable {
        return Completable.fromCallable { local.updateExams(exams) }
    }
}
