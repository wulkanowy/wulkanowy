package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.api.AdminMessageService
import io.github.wulkanowy.data.db.dao.AdminMessageDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.networkBoundResource
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminMessageRepository @Inject constructor(
    private val adminMessageService: AdminMessageService,
    private val adminMessageDao: AdminMessageDao,
    private val appInfo: AppInfo
) {
    private val saveFetchResultMutex = Mutex()

    suspend fun getAdminMessages(student: Student) = networkBoundResource(
        mutex = saveFetchResultMutex,
        query = { adminMessageDao.loadAll() },
        fetch = { adminMessageService.getAdminMessages() },
        shouldFetch = { true },
        saveFetchResult = adminMessageDao::removeOldAndSaveNew,
        showSavedOnLoading = false
    ).map { adminMessagesResource ->
        val adminMessages = adminMessagesResource.data

        val validAdminMessage = adminMessages
            ?.filter { adminMessage ->
                val isCorrectRegister =
                    adminMessage.targetRegisterHost?.contains(student.scrapperBaseUrl, true) ?: true
                val isCorrectFlavor =
                    adminMessage.targetFlavor?.equals(appInfo.buildFlavor, true) ?: true
                val isCorrectMaxVersion =
                    adminMessage.versionMax?.let { it >= appInfo.versionCode } ?: true
                val isCorrectMinVersion =
                    adminMessage.versionMin?.let { it <= appInfo.versionCode } ?: true

                isCorrectRegister && isCorrectFlavor && isCorrectMaxVersion && isCorrectMinVersion
            }
            ?.maxByOrNull { it.id }

        return@map Resource(
            status = adminMessagesResource.status,
            data = validAdminMessage,
            error = adminMessagesResource.error
        )
    }
}
