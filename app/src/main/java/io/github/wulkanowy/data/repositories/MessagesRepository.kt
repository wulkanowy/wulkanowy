package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Semester
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

    fun getMessages(semester: Semester, forceRefresh: Boolean = false): Single<List<Message>> {
        return local.getMessages(semester).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getMessages(semester)
                    else Single.error(UnknownHostException())
                }.flatMap { newMessages ->
                    local.getMessages(semester)
                        .toSingle(emptyList())
                        .doOnSuccess { oldMessages ->
                            local.deleteMessages(oldMessages - newMessages)
                            local.saveMessages(newMessages - oldMessages)
                        }
                }.flatMap {
                    local.getMessages(semester).toSingle(emptyList())
                })
    }

    fun getNumberOfMessages(semester: Semester, senderId: Int): Single<Int> {
        return local.getNumberOfMessages(semester, senderId)
    }

    fun getMessagesBySenderId(semester: Semester, senderId: Int, start: Int): Single<List<Message>> {
        return local.getMessagesBySenderId(semester, senderId, start)
            .filter { messages -> messages.none { it.content.isNullOrEmpty() } }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) local.getMessagesBySenderId(semester, senderId, start).toSingle(emptyList())
                    else Single.error(UnknownHostException())
                }
                .map { messages -> messages.filter { it.content.isNullOrEmpty() } }
                .flatMap { dbMessages ->
                    remote.getMessagesContent(semester, dbMessages)
                        .doOnSuccess { new ->
                            local.updateMessages(dbMessages.map { message ->
                                message.copy(content = new.single { it.realId == message.messageID }.content)
                                    .apply { id = message.id }
                            }).subscribe()
                        }
                }.flatMap {
                    local.getMessagesBySenderId(semester, senderId, start).toSingle(emptyList())
                }
            )
    }
}
