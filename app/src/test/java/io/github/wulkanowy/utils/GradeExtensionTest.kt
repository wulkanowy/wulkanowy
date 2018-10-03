package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class GradeExtensionTest {

    @Test
    fun calcWeightedAverage() {
        assertEquals(3.47, listOf(
                Grade("", "", "", "", 5, 0.33
                        , "", "", "", "", "",
                        6, "", ""),
                Grade("", "", "", "", 5, -0.33
                        , "", "", "", "", "",
                        5, "", ""),
                Grade("", "", "", "", 4, 0.0
                        , "", "", "", "", "",
                        1, "", ""),
                Grade("", "", "", "", 1, 0.5
                        , "", "", "", "", "",
                        9, "", ""),
                Grade("", "", "", "", 0, 0.0
                        , "", "", "", "", "",
                        0, "", "")
        ).calcAverage(), 0.005)
    }

    @Test
    fun calcSummaryAverage() {
        assertEquals(2.5, listOf(
                GradeSummary(0, "", "", "", "",
                        "5"),
                GradeSummary(0, "", "", "", "",
                        "-5"),
                GradeSummary(0, "", "", "", "",
                        "test"),
                GradeSummary(0, "", "", "", "",
                        "0")
        ).calcAverage(), 0.005)
    }
}
