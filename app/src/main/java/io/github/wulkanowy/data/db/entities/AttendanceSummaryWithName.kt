package io.github.wulkanowy.data.db.entities

import androidx.room.Embedded

data class AttendanceSummaryWithName(val name: String, @Embedded val attendanceSummary: AttendanceSummary)