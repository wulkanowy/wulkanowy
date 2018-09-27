package io.github.wulkanowy.utils.extension

import io.github.wulkanowy.utils.DATE_PATTERN
import io.github.wulkanowy.utils.isHolidays
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.TemporalAdjusters
import java.util.*

private val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)

fun LocalDate.toDate(): Date = java.sql.Date.valueOf(this.format(formatter))

fun LocalDate.toFormat(format: String): String = this.format(DateTimeFormatter.ofPattern(format))

fun LocalDate.toFormat(): String = this.toFormat(DATE_PATTERN)

fun LocalDate.isHolidays(): Boolean = isHolidays(this)

fun LocalDate.getNearSchoolDay(): LocalDate {
    return when(this.dayOfWeek) {
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> this.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        else -> this
    }
}

fun LocalDate.getNextSchoolDay(): LocalDate {
    return when(this.dayOfWeek) {
        DayOfWeek.FRIDAY -> this.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        else -> this.plusDays(1)
    }
}

fun LocalDate.getPreviousSchoolDay(): LocalDate {
    return when(this.dayOfWeek) {
        DayOfWeek.MONDAY -> this.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY))
        else -> this.minusDays(1)
    }
}

fun Date.toLocalDate(): LocalDate = Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDate()

fun Date.toLocalDateTime(): LocalDateTime = Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun Date.getWeekDayName(): String = this.toLocalDate().format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))

fun Date.toFormat(): String = this.toLocalDate().toFormat()

fun Date.toFormatTime(format: String): String = this.toLocalDateTime().format(DateTimeFormatter.ofPattern(format))
