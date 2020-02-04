package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Semester
import org.threeten.bp.LocalDate.now

inline val Semester.isCurrent: Boolean
    get() = now() in start..end
