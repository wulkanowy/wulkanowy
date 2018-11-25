package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.toLocalDateTime
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

    fun getMessageContent(student: Student, message: Message, markAsRead: Boolean = false): Single<Message> {
        return api.getMessage(message.messageID ?: 0, message.folderId ?: 0, markAsRead, message.realId ?: 0)
            .map {
                Message(studentId = student.studentId, realId = it.id).apply {
                    content = it.content
                }
            }
    }
}
