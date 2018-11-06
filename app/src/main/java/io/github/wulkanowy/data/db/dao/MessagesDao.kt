package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.wulkanowy.data.db.entities.Message
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface MessagesDao {

    @Insert
    fun insertAll(messages: List<Message>): List<Long>

    @Delete
    fun deleteAll(messages: List<Message>)

    @Update
    fun updateAll(messages: List<Message>)

    @Query("SELECT * FROM Messages WHERE student_id = :studentId")
    fun getAll(studentId: Int): Maybe<List<Message>>

    @Query("SELECT * FROM Messages WHERE student_id = :studentId ORDER BY date DESC LIMIT 0, 1")
    fun getLast(studentId: Int): Maybe<Message>

    @Query("SELECT * FROM Messages WHERE unread = 1 AND student_id = :studentId")
    fun getNewMessages(studentId: Int): Maybe<List<Message>>

    @Query("SELECT COUNT(id) FROM Messages WHERE student_id = :studentId AND sender_id = :senderId")
    fun getNumberOfMessages(studentId: Int, senderId: Int): Single<Int>

    @Query("SELECT * FROM Messages WHERE student_id = :studentId AND conversation_id = :conversationId ORDER BY date DESC LIMIT :start, :end")
    fun getByConversationId(studentId: Int, conversationId: Int, start: Int, end: Int): Maybe<List<Message>>
}
