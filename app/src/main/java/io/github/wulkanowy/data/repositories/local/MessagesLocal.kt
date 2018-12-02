package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesLocal @Inject constructor(private val messagesDb: MessagesDao) {

    fun getMessage(id: Long): Maybe<List<Message>> {
        return messagesDb.get(id)
    }

    fun getMessages(studentId: Int, folderId: Int): Maybe<List<Message>> {
        return when(folderId) {
            3 -> messagesDb.loadDeleted(studentId)
            else -> messagesDb.load(studentId, folderId)
        }.filter { !it.isEmpty() }
    }

    fun getNewMessages(student: Student): Maybe<List<Message>> {
        return messagesDb.getNewMessages(student.studentId)
    }

    fun saveMessages(messages: List<Message>): List<Long> {
        return messagesDb.insertAll(messages)
    }

    fun updateMessages(messages: List<Message>): Completable {
        return Completable.fromCallable { messagesDb.updateAll(messages) }
    }

    fun deleteMessages(messages: List<Message>) {
        messagesDb.deleteAll(messages)
    }
}
