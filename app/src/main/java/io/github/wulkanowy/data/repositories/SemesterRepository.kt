package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.local.SemesterLocal
import io.github.wulkanowy.data.repositories.remote.SemesterRemote
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterRepository @Inject constructor(
    private val remote: SemesterRemote,
    private val local: SemesterLocal,
    private val settings: InternetObservingSettings
) {

    fun getSemesters(student: Student, forceRefresh: Boolean = false): Single<List<Semester>> {
        return local.getSemesters(student).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getSemesters(student) else Single.error(UnknownHostException())
                }.map { newSemesters ->
                    local.apply {
                        saveSemesters(newSemesters)
                        setCurrentSemester(newSemesters.single { it.isCurrent }.semesterId)
                    }
                }.flatMap { local.getSemesters(student).toSingle(emptyList()) })
    }

    fun getCurrentSemester(student: Student): Single<Semester> {
        return local.getSemesters(student).map { semesters -> semesters.single { it.isCurrent } }.toSingle()
    }
}

