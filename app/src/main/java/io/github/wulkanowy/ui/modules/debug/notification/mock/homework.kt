package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.Homework
import java.time.LocalDate

val debugHomeworkItems = listOf(
    Homework(
        semesterId = 0,
        studentId = 0,
        date = LocalDate.now(),
        entryDate = LocalDate.now(),
        subject = "",
        content = "",
        teacher = "",
        teacherSymbol = "",
        attachments = listOf(),
    )
)
