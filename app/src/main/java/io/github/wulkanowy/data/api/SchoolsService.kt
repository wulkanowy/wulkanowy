package io.github.wulkanowy.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Singleton

@Singleton
interface SchoolsService {

    @POST("/performCommand")
    suspend fun performCommand(@Body request: IntegrityRequest<LoginEvent>)
}

@Serializable
data class LoginEvent(
    val uuid: String,
    val schoolName: String,
    val schoolAddress: String,
    val scraperBaseUrl: String,
    val symbol: String,
    val schoolId: String,
    val loginType: String,
)

@Serializable
data class IntegrityRequest<T>(
    val tokenString: String,
    val data: T,
)
