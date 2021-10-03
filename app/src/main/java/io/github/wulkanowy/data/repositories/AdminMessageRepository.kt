package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.api.AdminMessageService
import io.github.wulkanowy.data.db.dao.AdminMessageDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminMessageRepository @Inject constructor(
    private val adminMessageService: AdminMessageService,
    private val adminMessageDao: AdminMessageDao
) {

    suspend fun getAdminMessages() = adminMessageService.getAdminMessages()
}
