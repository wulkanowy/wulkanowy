package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.LuckyNumber
import java.time.LocalDate

val debugLuckyNumber = LuckyNumber(
    studentId = 0,
    date = LocalDate.now(),
    luckyNumber = 0,
)
