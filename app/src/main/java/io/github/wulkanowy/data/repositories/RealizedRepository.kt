package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Realized
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.RealizedLocal
import io.github.wulkanowy.data.repositories.remote.RealizedRemote
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealizedRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: RealizedLocal,
    private val remote: RealizedRemote
) {

    fun getRealized(semester: Semester, startDate: LocalDate, endDate: LocalDate, forceRefresh: Boolean = false): Single<List<Realized>> {
        return Single.fromCallable { startDate.monday to endDate.friday }
            .flatMap { dates ->
                local.getRealized(semester, dates.first, dates.second).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getRealized(semester, dates.first, dates.second)
                            else Single.error(UnknownError())
                        }.flatMap { new ->
                            local.getRealized(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                                .doOnSuccess { old ->
                                    local.deleteExams(old - new)
                                    local.saveRealized(new - old)
                                }
                        }.flatMap {
                            local.getRealized(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                        }).map { list -> list.filter { it.date in startDate..endDate } }
            }
    }
}
