package io.github.wulkanowy.domain.adminmessage

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mapResourceData
import io.github.wulkanowy.data.repositories.AdminMessageRepository
import io.github.wulkanowy.utils.AppInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppropriateAdminMessageUseCase @Inject constructor(
    private val adminMessageRepository: AdminMessageRepository,
    private val appInfo: AppInfo
) {

    operator fun invoke(student: Student): Flow<Resource<AdminMessage?>> {
        return adminMessageRepository.getAdminMessages().mapResourceData { adminMessages ->
            adminMessages
                .filter { it.isAppropriate(student) }
                .maxByOrNull { it.id }
        }
    }

    private fun AdminMessage.isAppropriate(student: Student): Boolean {
        val isCorrectRegister = targetRegisterHost?.let {
            student.scrapperBaseUrl.contains(it, true)
        } ?: true
        val isCorrectFlavor = targetFlavor?.equals(appInfo.buildFlavor, true) ?: true
        val isCorrectMaxVersion = versionMax?.let { it >= appInfo.versionCode } ?: true
        val isCorrectMinVersion = versionMin?.let { it <= appInfo.versionCode } ?: true

        return isCorrectRegister && isCorrectFlavor && isCorrectMaxVersion && isCorrectMinVersion
    }
}
