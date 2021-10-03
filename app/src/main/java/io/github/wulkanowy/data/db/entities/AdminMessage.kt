package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "AdminMessages")
data class AdminMessage(

    @PrimaryKey
    @Json(name = "id")
    val id: Int,

    @Json(name = "title")
    val title: String,

    @Json(name = "content")
    val content: String,

    @ColumnInfo(name = "version_name")
    @Json(name = "versionMin")
    val versionMin: Int? = null,

    @ColumnInfo(name = "version_max")
    @Json(name = "versionMax")
    val versionMax: Int? = null,

    @ColumnInfo(name = "target_register_host")
    @Json(name = "targetRegisterHost")
    val targetRegisterHost: String? = null,

    @ColumnInfo(name = "target_flavor")
    @Json(name = "targetFlavor")
    val targetFlavor: String? = null,

    @ColumnInfo(name = "destination_url")
    @Json(name = "destinationUrl")
    val destinationUrl: String? = null,

    @Json(name = "priority")
    val priority: String,

    @Json(name = "type")
    val type: String
)