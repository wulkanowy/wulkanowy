package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

fun List<SdkRecipient>.mapToEntities(userLoginId: Int) = map {
    Recipient(
        studentId = userLoginId,
        realId = it.mailboxGlobalKey,
        realName = it.name,
        name = it.name,
        hash = "",//it.hash,
        loginId = 0,//it.loginId,
        role = 2, //it.role,
        unitId = 0, //it.reportingUnitId ?: 0
    )
}
