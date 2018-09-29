package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.AttendanceLocal
import io.github.wulkanowy.data.repositories.remote.AttendanceRemote
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
        private val settings: InternetObservingSettings,
        private val local: AttendanceLocal,
        private val remote: AttendanceRemote
) {

    fun getAttendance(semester: Semester, date: LocalDate, forceRefresh: Boolean = false): Single<List<Attendance>> {
        return local.getAttendance(semester, date).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getAttendance(semester, date)
                            else Single.error(UnknownHostException())
                        }.flatMap { newAttendance ->
                            local.getAttendance(semester, date).toSingle(emptyList())
                                    .map {
                                        local.deleteAttendance(it - newAttendance)
                                        local.saveAttendance(newAttendance - it)

                                        newAttendance
                                    }
                        }
                )
    }
}
