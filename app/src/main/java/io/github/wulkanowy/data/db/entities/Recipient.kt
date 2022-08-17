package io.github.wulkanowy.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@kotlinx.serialization.Serializable
@Entity(tableName = "Recipients")
data class Recipient(

    @PrimaryKey
    val mailboxGlobalKey: String,
    val studentMailboxGlobalKey: String,
    val fullName: String,
    val name: String,
    val schoolShortName: String,
    val type: MailboxType,
) : Serializable {
    override fun toString() = name
}
