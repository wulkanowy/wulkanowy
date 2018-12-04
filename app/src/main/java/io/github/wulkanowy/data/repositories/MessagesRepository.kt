package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.local.MessagesLocal
import io.github.wulkanowy.data.repositories.remote.MessagesRemote
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

    enum class MessageFolder(val id: Int = 1) {
        RECEIVED(1),
        SENT(2),
        TRASHED(3)
    }

    fun getMessages(studentId: Int, folder: MessageFolder, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Message>> {
        return local.getMessages(studentId, folder).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getMessages(studentId, folder)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getMessages(studentId, folder).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteMessages(old - new)
                            local.saveMessages((new - old)
                                .onEach {
                                    if (notify) it.isNotified = false
                                })
                        }
                }.flatMap { local.getMessages(studentId, folder).toSingle(emptyList()) }
            )
    }

    fun getMessage(studentId: Int, id: Long): Single<List<Message>> {
        return local.getMessage(id)
            .filter { messages -> messages.none { it.content.isNullOrEmpty() } }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) local.getMessage(id).toSingle(emptyList())
                    else Single.error(UnknownHostException())
                }
                .map { messages -> messages.filter { it.content.isNullOrEmpty() } }
                .flatMap { dbMessages ->
                    remote.getMessagesContent(studentId, dbMessages)
                        .doOnSuccess { new ->
                            local.updateMessages(dbMessages.map { message ->
                                message.copy(unread = false).apply {
                                    this.id = message.id
                                    content = new.single { this.realId == message.realId }.content
                                }
                            })
                        }
                }.flatMap {
                    local.getMessage(id).toSingle(emptyList())
                }
            )
    }

    fun getNewMessages(student: Student): Single<List<Message>> {
        return local.getNewMessages(student).toSingle(emptyList())
    }

    fun updateMessages(messages: List<Message>) {
        local.updateMessages(messages)
    }
}
