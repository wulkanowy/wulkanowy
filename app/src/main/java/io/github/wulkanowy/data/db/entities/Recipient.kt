package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Recipients")
data class Recipient(

    @ColumnInfo(name = "student_id")
    var studentId: Int,

    @ColumnInfo(name = "real_id")
    var realId: String,

    var name: String,

    @ColumnInfo(name = "login_id")
    var loginId: Int,

    @ColumnInfo(name = "unit_id")
    var unitId: Int,

    var role: Int,

    var hash: String

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
