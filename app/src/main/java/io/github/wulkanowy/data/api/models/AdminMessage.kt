package io.github.wulkanowy.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdminMessage(

    @Json(name = "id")
    val id: Int,

    @Json(name = "title")
    val title: String,

    @Json(name = "content")
    val content: String,

    @Json(name = "type")
    val type: String
)