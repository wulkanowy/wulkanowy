package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.db.entities.MailboxType
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.pojo.Mailbox as SdkMailbox

fun List<SdkMailbox>.mapToEntities(student: Student) = map {
    Mailbox(
        globalKey = it.globalKey,
        userName = it.name,
        userLoginId = student.userLoginId,
        studentName = it.studentName,
        type = MailboxType.STUDENT // todo: add mailbox type in SDK
    )
}
