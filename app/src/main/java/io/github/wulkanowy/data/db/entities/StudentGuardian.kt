package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "StudentGuardians")
data class StudentGuardian(

    @ColumnInfo(name = "real_id")
    val realId: Int,

    val name: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    val kinship: String,

    val address: String,

    @ColumnInfo(name = "home_phone")
    val homePhone: String?,

    @ColumnInfo(name = "cell_phone")
    val cellPhone: String?,

    @ColumnInfo(name = "work_phone")
    val workPhone: String?,

    val email: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    val phone: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}