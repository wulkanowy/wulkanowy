package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.Conference
import java.time.LocalDateTime

val debugConferenceItems = listOf(
    Conference(
        studentId = 0,
        diaryId = 0,
        agenda = "",
        conferenceId = 0,
        date = LocalDateTime.now(),
        presentOnConference = "",
        subject = "",
        title = "",
    )
)
