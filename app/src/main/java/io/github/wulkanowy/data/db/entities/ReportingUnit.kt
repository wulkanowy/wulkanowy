package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ReportingUnits")
data class ReportingUnit(

    @ColumnInfo(name = "student_id")
    var studentId: Int? = null,

    @ColumnInfo(name = "real_id")
    var realId: Int = 0,

    var short: String = "",

    @ColumnInfo(name = "sender_id")
    var senderId: Int = 0,

    @ColumnInfo(name = "sender_name")
    var senderName: String = "",

    var roles: List<Int> = emptyList()

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
