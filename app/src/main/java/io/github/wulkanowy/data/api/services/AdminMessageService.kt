package io.github.wulkanowy.data.api.services

import io.github.wulkanowy.data.api.models.AdminMessage
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface AdminMessageService {

    @GET("/messages/messages.json")
    suspend fun getAdminMessages(): List<AdminMessage>
}