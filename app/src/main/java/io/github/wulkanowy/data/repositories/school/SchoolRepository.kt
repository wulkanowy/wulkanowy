package io.github.wulkanowy.data.repositories.school

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.SchoolInfo
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: SchoolLocal,
    private val remote: SchoolRemote
) {

    fun getSchoolInfo(semester: Semester, forceRefresh: Boolean = false): Maybe<SchoolInfo> {
        return local.getSchoolInfo(semester).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getSchoolInfo(semester)
                    else Single.error(UnknownHostException())
                }.flatMapMaybe { new ->
                    local.getSchoolInfo(semester)
                        .doOnSuccess { old ->
                            if (new != old) {
                                local.deleteSchoolInfo(old)
                                local.saveSchoolInfo(new)
                            }
                        }
                        .doOnComplete {
                            local.saveSchoolInfo(new)
                        }
                }.flatMap({ local.getSchoolInfo(semester) }, { Maybe.error(it) },
                    { local.getSchoolInfo(semester) })
            )
    }
}
