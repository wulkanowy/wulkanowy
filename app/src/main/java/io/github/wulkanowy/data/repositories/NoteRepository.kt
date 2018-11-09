package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.NoteLocal
import io.github.wulkanowy.data.repositories.remote.NoteRemote
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: NoteLocal,
    private val remote: NoteRemote
) {

    fun getNotes(semester: Semester, forceRefresh: Boolean = false): Single<List<Note>> {
        return local.getNotes(semester).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getNotes(semester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getNotes(semester).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteNotes(old - new)
                            local.saveNotes(new - old)
                        }
                }.flatMap { local.getNotes(semester).toSingle(emptyList()) }
            )
    }
}
