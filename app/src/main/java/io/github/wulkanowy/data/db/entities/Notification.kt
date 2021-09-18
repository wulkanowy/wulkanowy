package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notifications")
data class Notification(

    @ColumnInfo(name = "student_id")
    val studentId: Long,

    val title: String,

    val content: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}