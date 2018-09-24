package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.local.TimetableLocal
import io.github.wulkanowy.data.repositories.remote.TimetableRemote
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRepository @Inject constructor(
        private val settings: InternetObservingSettings,
        private val local: TimetableLocal,
        private val remote: TimetableRemote
) {

    fun getTimetable(semester: Semester, date: LocalDate, forceRefresh: Boolean = false): Single<List<Timetable>> {
        return local.getLessons(semester, date).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getLessons(semester, date)
                            else Single.error(UnknownHostException())
                        }.flatMap { newLessons ->
                            local.getLessons(semester, date).toSingle(emptyList())
                                    .map {
                                        local.deleteExams(it - newLessons)
                                        local.saveLessons(newLessons - it)

                                        newLessons
                                    }
                        }
                )
    }
}
