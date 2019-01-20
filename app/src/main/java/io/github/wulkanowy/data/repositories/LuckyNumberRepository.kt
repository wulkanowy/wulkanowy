package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.LuckyNumberLocal
import io.github.wulkanowy.data.repositories.remote.LuckyNumberRemote
import io.reactivex.Maybe
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

    fun getLuckyNumber(semester: Semester, forceRefresh: Boolean = false): Maybe<LuckyNumber> {
        return local.getLuckyNumber(semester, LocalDate.now()).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMapMaybe {
                    if (it) remote.getLuckyNumber(semester)
                    else Maybe.error(UnknownHostException())
                }.flatMap { new ->
                    local.getLuckyNumber(semester, LocalDate.now())
                        .doOnSuccess { old ->
                            if (new != old) {
                                local.deleteLuckyNumber(old)
                                local.saveLuckyNumber(new)
                            }
                        }
                        .doOnComplete {
                            local.saveLuckyNumber(new)
                        }
                }.flatMap { local.getLuckyNumber(semester, LocalDate.now()) }
            )
    }
}
