package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class StudentIsAuthorizedAndEduOne(

    @ColumnInfo(name = "is_authorized", defaultValue = "0")
    val isAuthorized: Boolean,

    @ColumnInfo(name = "is_edu_one", defaultValue = "0")
    val isEduOne: Boolean,
) : Serializable {

    @PrimaryKey
    var id: Long = 0
}
