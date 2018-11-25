package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.local.MessagesLocal
import io.github.wulkanowy.data.repositories.remote.MessagesRemote
import io.reactivex.Completable
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: MessagesLocal,
    private val remote: MessagesRemote
) {

    fun getReceivedMessages(studentId: Int, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Message>> {
        return getMessages(studentId, 1, forceRefresh, notify)
    }

    fun getSentMessages(studentId: Int, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Message>> {
        return getMessages(studentId, 2, forceRefresh, notify)
    }

    fun getTrashedMessages(studentId: Int, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Message>> {
        return getMessages(studentId, 3, forceRefresh, notify)
    }

    private fun getMessages(studentId: Int, folderId: Int, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Message>> {
        return local.getMessages(studentId, folderId).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getMessages(studentId, folderId)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getMessages(studentId, 1).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteMessages(old - new)
                            local.saveMessages((new - old)
                                .onEach {
                                    if (notify) it.isNotified = false
                                })
                        }
                }.flatMap { local.getMessages(studentId, 1).toSingle(emptyList()) }
            )
    }

    fun getNewMessages(student: Student): Single<List<Message>> {
        return local.getNewMessages(student).toSingle(emptyList())
    }

    fun updateMessages(messages: List<Message>): Completable {
        return local.updateMessages(messages)
    }
}
