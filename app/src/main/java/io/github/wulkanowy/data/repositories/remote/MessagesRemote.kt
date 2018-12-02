package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.utils.toLocalDateTime
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import io.github.wulkanowy.api.messages.Message as ApiMessage

class MessagesRemote @Inject constructor(private val api: Api) {

    fun getMessages(studentId: Int, folderId: Int): Single<List<Message>> {
        return when (folderId) {
            1 -> api.getReceivedMessages()
            2 -> api.getSentMessages()
            else -> api.getDeletedMessages()
        }.map { messages ->
            messages.map {
                Message(
                    studentId = studentId,
                    realId = it.id,
                    messageId = it.messageId,
                    sender = it.sender,
                    senderId = it.senderId,
                    recipient = it.recipient,
                    recipientId = it.recipientId,
                    subject = it.subject.trim(),
                    date = it.date?.toLocalDateTime(),
                    folderId = it.folderId,
                    unread = it.unread,
                    unreadBy = it.unreadBy,
                    readBy = it.readBy,
                    removed = it.removed
                )
            }
        }
    }

    fun getMessagesContent(studentId: Int, messages: List<Message>, markAsRead: Boolean = false): Single<List<Message>> {
        return Observable.fromIterable(messages)
            .flatMapSingle { api.getMessage(it.messageId ?: 0, it.folderId, markAsRead, it.realId ?: 0) }
            .map {
                Message(studentId = studentId, realId = it.id).apply {
                    content = it.content?.trim()
                }
            }.toList()
    }
}
