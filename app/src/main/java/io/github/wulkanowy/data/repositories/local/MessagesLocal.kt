package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesLocal @Inject constructor(private val messagesDb: MessagesDao) {

    fun getMessages(semester: Semester): Maybe<List<Message>> {
        return messagesDb.getAll(semester.studentId).filter { !it.isEmpty() }
    }

    fun getNewMessages(semester: Semester): Maybe<List<Message>> {
        return messagesDb.getNewMessages(semester.studentId)
    }

    fun getNumberOfMessages(semester: Semester, senderId: Int): Single<Int> {
        return messagesDb.getNumberOfMessages(semester.studentId, senderId)
    }

    fun getMessagesByConversationId(semester: Semester, conversationId: Int, start: Int, end: Int): Maybe<List<Message>> {
        return messagesDb.getByConversationId(semester.studentId, conversationId, start, end).filter { !it.isEmpty() }
    }

    fun saveMessages(messages: List<Message>) {
        messagesDb.insertAll(messages)
    }

    fun deleteMessages(messages: List<Message>) {
        messagesDb.deleteAll(messages)
    }

    fun updateMessages(messages: List<Message>): Completable {
        return Completable.fromCallable { messagesDb.updateAll(messages) }
    }
}
