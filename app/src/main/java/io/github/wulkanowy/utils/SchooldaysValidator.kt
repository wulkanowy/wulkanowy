package io.github.wulkanowy.utils

import com.google.android.material.datepicker.CalendarConstraints
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Parcelize
class SchoolDaysValidator : CalendarConstraints.DateValidator {

    override fun isValid(dateLong: Long): Boolean {
        val now = LocalDate.now()
        val date = dateLong.toLocalDateTime()

        val startYear = if (now.monthValue <= 6) now.year - 1 else now.year
        val startOfSchoolYear = now.withYear(startYear).firstSchoolDay
        val endYear = if (now.monthValue > 6) now.year + 1 else now.year
        val endOfSchoolYear = now.withYear(endYear).lastSchoolDay

        return date.toLocalDate().until(endOfSchoolYear, ChronoUnit.DAYS) >= 0 && date.toLocalDate()
            .until(startOfSchoolYear, ChronoUnit.DAYS) <= 0 && date.dayOfWeek != DayOfWeek.SUNDAY
    }
}