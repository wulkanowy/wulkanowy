package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.api.services.AdminMessageService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminMessageRepository @Inject constructor(private val adminMessageService: AdminMessageService) {

    suspend fun getAdminMessages() = adminMessageService.getAdminMessages()
}
