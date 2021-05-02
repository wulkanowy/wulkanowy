package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.DirectorInformation
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.pojo.DirectorInformation as SdkDirectorInformation

fun List<SdkDirectorInformation>.mapToEntities(student: Student) = map {
    DirectorInformation(
        studentId = student.studentId,
        date = it.date,
        subject = it.subject,
        content = it.content,
    )
}
