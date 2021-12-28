package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.wulkanowy.services.sync.notifications.NotificationType
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity(tableName = "Notifications")
data class Notification(

    @ColumnInfo(name = "student_id")
    val studentId: Long,

    val title: String,

    val content: String,

    val type: NotificationType,

    @Deprecated("use dateZoned instead")
    val date: LocalDateTime,

    @ColumnInfo(name = "date_zoned", defaultValue = "0")
    val dateZoned: ZonedDateTime,

    val data: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}