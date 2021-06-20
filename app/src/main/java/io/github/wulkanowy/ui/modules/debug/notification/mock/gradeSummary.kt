package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.GradeSummary

val debugGradeSummaryItems = listOf(
    GradeSummary(
        semesterId = 0,
        studentId = 0,
        position = 0,
        subject = "",
        predictedGrade = "",
        finalGrade = "",
        proposedPoints = "",
        finalPoints = "",
        pointsSum = "",
        average = .0
    ),
)
