package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.MessagesLocal
import io.github.wulkanowy.data.repositories.remote.MessagesRemote
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.threeten.bp.DayOfWeek.MONDAY
import org.threeten.bp.LocalDate.now
import org.threeten.bp.temporal.TemporalAdjusters.next
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: MessagesLocal,
    private val remote: MessagesRemote
) {

    fun getMessages(semester: Semester, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Message>> {
        return local.getMessages(semester).filter { !forceRefresh }
            .switchIfEmpty(local.getLastMessage(semester).switchIfEmpty(Maybe.just(Message())).toSingle()
                .flatMap { lastMessage ->
                    ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getMessages(semester, lastMessage.date, now().with(next(MONDAY)).atStartOfDay())
                            else Single.error(UnknownHostException())
                        }
                }.map { newMessages ->
                    local.saveMessages(newMessages.onEach {
                        if (notify) it.isNotified = false
                    })
                }.flatMap {
                    local.getMessages(semester).toSingle(emptyList())
                }
            )
    }

    fun getNewMessages(semester: Semester): Single<List<Message>> {
        return local.getNewMessages(semester).toSingle(emptyList())
    }

    fun updateMessages(messages: List<Message>): Completable {
        return local.updateMessages(messages)
    }

    fun getNumberOfMessages(semester: Semester, senderId: Int): Single<Int> {
        return local.getNumberOfMessages(semester, senderId)
    }

    fun getMessagesByConversationId(semester: Semester, conversationId: Int, start: Int, end: Int): Single<List<Message>> {
        return local.getMessagesByConversationId(semester, conversationId, start, end)
            .filter { messages -> messages.none { it.content.isNullOrEmpty() } }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) local.getMessagesByConversationId(semester, conversationId, start, end).toSingle(emptyList())
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
                    local.getMessagesByConversationId(semester, conversationId, start, end).toSingle(emptyList())
                }
            )
    }
}
