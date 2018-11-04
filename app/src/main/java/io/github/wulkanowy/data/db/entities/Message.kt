package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.io.Serializable

@Entity(tableName = "Messages")
data class Message(

    @ColumnInfo(name = "student_id")
    var studentId: Int,

    @ColumnInfo(name = "diary_id")
    var diaryId: Int,

    @ColumnInfo(name = "conversation_id")
    val conversationId: Int,

    @ColumnInfo(name = "real_id")
    val realId: Int?,

    @ColumnInfo(name = "message_id")
    val messageID: Int?,

    @ColumnInfo(name = "sender_id")
    val senderID: Int?,

    @ColumnInfo(name = "userName")
    val sender: String?,

    val unread: Boolean?,

    val date: LocalDateTime?,

    val content: String?,

    val subject: String?,

    @ColumnInfo(name = "folder_id")
    val folderId: Int
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
