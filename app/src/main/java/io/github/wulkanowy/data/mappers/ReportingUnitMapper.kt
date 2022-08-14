package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.pojo.Mailbox as SdkMailbox

fun List<SdkMailbox>.mapToEntities(student: Student) = map {
    ReportingUnit(
        studentId = student.id.toInt(),
        unitId = 0, //it.id,
        roles = listOf(), //it.roles,
        senderId = 0,//it.senderId,
        senderName = it.globalKey,//it.senderName,
        shortName = "",//it.short
    )
}
