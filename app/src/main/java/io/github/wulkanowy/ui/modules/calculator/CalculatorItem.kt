package io.github.wulkanowy.ui.modules.calculator

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class CalculatorItem(
    var grade: Double,
    var weight: Double,
    var title: String? = null,
    @Contextual()
    var date: LocalDate? = null,
    var originalGrade: String? = null
) : java.io.Serializable

