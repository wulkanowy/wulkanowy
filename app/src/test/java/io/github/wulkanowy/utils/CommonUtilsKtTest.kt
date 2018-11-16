package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.AttendanceSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class AttendanceExtensionTest {

    @Test fun calculateAttendanceFromTypesTest() {
        val types = mutableListOf<AttendanceSummary>()

        for (i in 1..10) {
            val type = AttendanceSummary(1, 1, "Obecność", 1, 1, i)

            types.add(type)
        }

        for (i in 1..10) {
            val type = AttendanceSummary(1, 1, "Nieobecność nieusprawiedliwiona", 1, 1, i)

            types.add(type)
        }

        assertEquals(50.00, calculateAttendance(types), 0.0)
    }
}
