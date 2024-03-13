package io.github.wulkanowy.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class MessageWithAttachment(
    @Embedded
    val message: Message,

    @Relation(parentColumn = "message_global_key", entityColumn = "message_global_key")
    val attachments: List<MessageAttachment>,

    @Relation(parentColumn = "correspondents", entityColumn = "author")
    val mutedMessageSender: MutedMessageSender?,
) : Serializable
