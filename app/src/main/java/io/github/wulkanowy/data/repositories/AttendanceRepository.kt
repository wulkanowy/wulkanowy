package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.AttendanceLocal
import io.github.wulkanowy.data.repositories.remote.AttendanceRemote
import io.github.wulkanowy.utils.extension.toDate
import io.github.wulkanowy.utils.getNearMonday
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
        private val settings: InternetObservingSettings,
        private val local: AttendanceLocal,
        private val remote: AttendanceRemote
) {

    fun getAttendance(semester: Semester, start: LocalDate, end: LocalDate = start, forceRefresh: Boolean = false): Single<List<Attendance>> {
        return local.getAttendance(semester, start, end).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getAttendance(semester, getNearMonday(start), end)
                            else Single.error(UnknownHostException())
                        }.flatMap { newLessons ->
                            local.getAttendance(semester, getNearMonday(start), end.plusDays(5)).toSingle(emptyList())
                                    .map {
                                        local.deleteAttendance(it - newLessons)
                                        local.saveAttendance(newLessons - it)

                                        newLessons
                                    }
                        }.map { list -> list.asSequence()
                                .filter { it.date.time <= start.toDate().time && it.date.time >= end.toDate().time }
                                .toList()
                        }
                )
    }
}
