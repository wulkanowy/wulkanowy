package io.github.wulkanowy.data.repositories.message

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.message.MessageFolder.RECEIVED
import io.github.wulkanowy.sdk.pojo.SentMessage
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val local: MessageLocal,
    private val remote: MessageRemote
) {

    suspend fun refreshMessages(student: Student, semester: Semester, folder: MessageFolder, notify: Boolean = false) {
        val new = remote.getMessages(student, semester, folder)
        val old = local.getMessages(student, folder).first()

        local.deleteMessages(old uniqueSubtract new)
        local.saveMessages((new uniqueSubtract old).onEach {
            it.isNotified = !notify
        })
    }

    fun getMessages(student: Student, semester: Semester, folder: MessageFolder, notify: Boolean = false): Flow<List<Message>> {
        return local.getMessages(student, folder).map {
            if (it.isNotEmpty()) return@map it
            refreshMessages(student, semester, folder, notify)
            it
        }
    }

    suspend fun getMessage(student: Student, message: Message, markAsRead: Boolean = false): MessageWithAttachment {
        return local.getMessageWithAttachment(student, message).let {
            if (it.message.content.isNotEmpty().also { status ->
                    Timber.d("Message content in db empty: ${!status}")
                } && !it.message.unread) {
                return@let it
            }

            val dbMessage = local.getMessageWithAttachment(student, message)
            val (downloadedMessage, attachments) = remote.getMessagesContentDetails(student, dbMessage.message, markAsRead)

            local.updateMessages(listOf(dbMessage.message.copy(unread = !markAsRead).apply {
                id = dbMessage.message.id
                content = content.ifBlank { downloadedMessage }
            }))
            local.saveMessageAttachments(attachments)
            Timber.d("Message ${message.messageId} with blank content: ${dbMessage.message.content.isBlank()}, marked as read")

            local.getMessageWithAttachment(student, message)
        }
    }

    fun getNotNotifiedMessages(student: Student): Flow<List<Message>> {
        return local.getMessages(student, RECEIVED).map { it.filter { message -> !message.isNotified && message.unread } }
    }

    suspend fun updateMessages(messages: List<Message>) {
        return local.updateMessages(messages)
    }

    suspend fun sendMessage(student: Student, subject: String, content: String, recipients: List<Recipient>): SentMessage {
        return remote.sendMessage(student, subject, content, recipients)
    }

    suspend fun deleteMessage(student: Student, message: Message) {
        val isDeleted = remote.deleteMessage(student, message)

        if (!message.removed) local.updateMessages(listOf(message.copy(removed = isDeleted).apply {
            id = message.id
            content = message.content
        })) else local.deleteMessages(listOf(message))
    }
}
