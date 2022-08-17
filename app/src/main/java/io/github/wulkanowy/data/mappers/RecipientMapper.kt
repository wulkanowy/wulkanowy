package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.MailboxType
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

fun List<SdkRecipient>.mapToEntities(studentMailboxGlobalKey: String) = map {
    Recipient(
        mailboxGlobalKey = it.mailboxGlobalKey,
        fullName = it.name, // todo: add field in sdk
        name = it.name,
        studentMailboxGlobalKey = studentMailboxGlobalKey,
        schoolShortName = it.schoolNameShort,
        type = MailboxType.EMPLOYEE, // todo
    )
}
