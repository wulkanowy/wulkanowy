package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.LuckyNumberLocal
import io.github.wulkanowy.data.repositories.remote.LuckyNumberRemote
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: LuckyNumberLocal,
    private val remote: LuckyNumberRemote
) {

    fun getLuckyNumbers(semester: Semester, forceRefresh: Boolean = false): Single<List<LuckyNumber>> {
        return local.getLuckyNumbers(semester, LocalDate.now()).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getLuckyNumbers(semester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getLuckyNumbers(semester, LocalDate.now()).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteLuckyNumbers(old - new)
                            local.saveLuckyNumbers(new - old)
                        }
                }.flatMap { local.getLuckyNumbers(semester, LocalDate.now()).toSingle(emptyList()) }
            )
    }
}
