package io.github.wulkanowy.data.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.dao.MessageAttachmentDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.*
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.enums.MessageFolder.RECEIVED
import io.github.wulkanowy.data.mappers.mapFromEntities
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.data.pojos.MessageDraft
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val messagesDb: MessagesDao,
    private val messageAttachmentDao: MessageAttachmentDao,
    private val sdk: Sdk,
    @ApplicationContext private val context: Context,
    private val refreshHelper: AutoRefreshHelper,
    private val sharedPrefProvider: SharedPrefProvider,
    private val json: Json,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "message"

    @Suppress("UNUSED_PARAMETER")
    fun getMessages(
        student: Student,
        mailbox: Mailbox,
        folder: MessageFolder,
        forceRefresh: Boolean,
        notify: Boolean = false,
    ): Flow<Resource<List<Message>>> = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(
                key = getRefreshKey(cacheKey, student, folder)
            )
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { messagesDb.loadAll(mailbox.globalKey, folder.id) },
        fetch = {
            sdk.init(student).getMessages(Folder.valueOf(folder.name)).mapToEntities(mailbox)
        },
        saveFetchResult = { old, new ->
            messagesDb.deleteAll(old uniqueSubtract new)
            messagesDb.insertAll((new uniqueSubtract old).onEach {
                it.isNotified = !notify
            })

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student, folder))
        }
    )

    fun getMessage(
        student: Student,
        message: Message,
        markAsRead: Boolean = false,
    ): Flow<Resource<MessageWithAttachment?>> = networkBoundResource(
        isResultEmpty = { it?.message?.content.isNullOrBlank() },
        shouldFetch = {
            checkNotNull(it) { "This message no longer exist!" }
            Timber.d("Message content in db empty: ${it.message.content.isEmpty()}")
            it.message.unread || it.message.content.isEmpty()
        },
        query = { messagesDb.loadMessageWithAttachment(message.messageGlobalKey) },
        fetch = {
            sdk.init(student).getMessageDetails(
                messageKey = it!!.message.messageGlobalKey,
            ).let { details ->
                details.content to details.attachments.mapToEntities(message.messageGlobalKey)
            }
        },
        saveFetchResult = { old, (downloadedMessage, attachments) ->
            checkNotNull(old) { "Fetched message no longer exist!" }
            messagesDb.updateAll(
                listOf(old.message.copy(
                    messageGlobalKey = old.message.messageGlobalKey,
                ).apply {
                    unread = !markAsRead
                    content = content.ifBlank { downloadedMessage }
                })
            )
            messageAttachmentDao.insertAttachments(attachments)
            Timber.d("Message ${message.messageId} with blank content: ${old.message.content.isBlank()}, marked as read")
        }
    )

    fun getMessagesFromDatabase(mailbox: Mailbox): Flow<List<Message>> {
        return messagesDb.loadAll(mailbox.globalKey, RECEIVED.id)
    }

    suspend fun updateMessages(messages: List<Message>) {
        return messagesDb.updateAll(messages)
    }

    suspend fun sendMessage(
        student: Student,
        subject: String,
        content: String,
        recipients: List<Recipient>,
        mailboxId: String,
    ) {
        sdk.init(student).sendMessage(
            subject = subject,
            content = content,
            recipients = recipients.mapFromEntities(),
            mailboxId = mailboxId,
        )
    }

    suspend fun deleteMessages(student: Student, messages: List<Message>) {
        val folderId = messages.first().folderId
        sdk.init(student).deleteMessages(messages = messages.map { it.messageGlobalKey })

        if (folderId != MessageFolder.TRASHED.id) {
            val deletedMessages = messages.map {
                it.copy(folderId = MessageFolder.TRASHED.id)
                    .copy(messageGlobalKey = it.messageGlobalKey)
                    .apply {
                        content = it.content
                    }
            }

            messagesDb.updateAll(deletedMessages)
        } else messagesDb.deleteAll(messages)
    }

    suspend fun deleteMessage(student: Student, message: Message) =
        deleteMessages(student, listOf(message))

    var draftMessage: MessageDraft?
        get() = sharedPrefProvider.getString(context.getString(R.string.pref_key_message_send_draft))
            ?.let { json.decodeFromString(it) }
        set(value) = sharedPrefProvider.putString(
            context.getString(R.string.pref_key_message_send_draft),
            value?.let { json.encodeToString(it) }
        )
}
