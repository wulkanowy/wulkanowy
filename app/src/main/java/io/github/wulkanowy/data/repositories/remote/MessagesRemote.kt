package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.utils.toLocalDateTime
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import io.github.wulkanowy.api.messages.Message as ApiMessage

class MessagesRemote @Inject constructor(private val api: Api) {

    fun getMessages(semester: Semester): Single<List<Message>> {
        return Single.just(api.run {
            if (diaryId != semester.diaryId) {
                diaryId = semester.diaryId
                notifyDataChanged()
            }
        }).flatMap { api.getReceivedMessages() }.mergeWith(api.getSentMessages()).map { messages ->
            messages.map {
                Message(
                    studentId = semester.studentId,
                    diaryId = semester.diaryId,
                    conversationId = it.conversationId,
                    conversationName = it.conversationName,
                    content = it.content,
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
        }.toList().map { it.flatten() }
    }

    fun getMessagesContent(semester: Semester, messages: List<Message>): Single<List<Message>> {
        return Single.just(api.run {
            if (diaryId != semester.diaryId) {
                diaryId = semester.diaryId
                notifyDataChanged()
            }
        }).flatMapObservable { Observable.fromIterable(messages) }
            .flatMapSingle { api.getMessage(it.messageID ?: 0, it.folderId) }
            .map {
                Message(
                    studentId = semester.studentId,
                    diaryId = semester.diaryId,
                    conversationId = it.conversationId,
                    conversationName = it.conversationName,
                    content = it.content,
                    date = it.date?.toLocalDateTime(),
                    folderId = it.folderId,
                    messageID = it.messageId,
                    realId = it.id,
                    sender = it.sender,
                    senderID = it.senderId,
                    subject = it.subject,
                    unread = it.unread
                )
            }.toList()
    }
}
