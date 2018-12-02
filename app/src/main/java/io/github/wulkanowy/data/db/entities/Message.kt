package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime
import java.io.Serializable

@Entity(tableName = "Messages")
data class Message(

    @ColumnInfo(name = "student_id")
    var studentId: Int? = null,

    @ColumnInfo(name = "real_id")
    val realId: Int? = null,

    @ColumnInfo(name = "message_id")
    val messageId: Int? = null,

    @ColumnInfo(name = "conversation_id")
    val conversationId: Int? = null,

    @ColumnInfo(name = "conversation_name")
    val conversationName: String? = null,

    @ColumnInfo(name = "recipient_id")
    val recipientId: Int? = null,

    @ColumnInfo(name = "recipient_name")
    val recipient: String? = null,

    @ColumnInfo(name = "sender_id")
    val senderId: Int? = null,

    @ColumnInfo(name = "sender_name")
    val sender: String? = null,

    val unread: Boolean? = false,

    val date: LocalDateTime? = null,

    val subject: String? = null,

    @ColumnInfo(name = "folder_id")
    val folderId: Int? = null
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true

    var content: String? = null
}
