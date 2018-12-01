package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.toLocalDateTime
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import io.github.wulkanowy.api.messages.Message as ApiMessage

class MessagesRemote @Inject constructor(private val api: Api) {

    fun getMessages(studentId: Int, folderId: Int): Single<List<Message>> {
        return when (folderId) {
            2 -> api.getSentMessages()
            3 -> api.getDeletedMessages()
            else -> api.getReceivedMessages()
        }.map { messages ->
            messages.map {
                Message(
                    studentId = studentId,
                    conversationId = it.conversationId,
                    conversationName = it.conversationName,
                    date = it.date?.toLocalDateTime(),
                    folderId = it.folderId,
                    messageID = it.messageId,
                    realId = it.id,
                    sender = it.sender,
                    senderID = it.senderId,
                    subject = it.subject,
                    unread = it.unread
                )
            }
        }
    }

    fun getMessagesContent(studentId: Int, messages: List<Message>, markAsRead: Boolean = false): Single<List<Message>> {
        return Observable.fromIterable(messages)
            .flatMapSingle { api.getMessage(it.messageID ?: 0, it.folderId ?: 0, markAsRead, it.realId ?: 0) }
            .map {
                Message(studentId = studentId, realId = it.id).apply {
                    content = it.content?.trim()
                }
            }.toList()
    }
}
