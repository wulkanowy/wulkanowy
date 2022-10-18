package io.github.wulkanowy.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Mailboxes")
data class Mailbox(

    @PrimaryKey
    val globalKey: String,
    val fullName: String,
    val userName: String,
    val email: String,
    val studentName: String,
    val schoolNameShort: String,
    val type: MailboxType,
) : java.io.Serializable, Parcelable

enum class MailboxType {
    STUDENT,
    PARENT,
    GUARDIAN,
    EMPLOYEE,
    UNKNOWN,
}
