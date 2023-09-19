package io.github.wulkanowy.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Singleton

@Singleton
interface SchoolsService {

    @POST("/performCommand")
    suspend fun performCommand(@Body body: ServerCommand): CommandResult
}

@Serializable
data class ServerCommand(
    val commandString: String,
    val tokenString: String
)

@Serializable
data class CommandResult(
    val commandSuccess: Boolean,
    val diagnosticMessage: String,
)
