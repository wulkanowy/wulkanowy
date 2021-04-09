package io.github.wulkanowy.utils

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class SchoolDaysValidator() : CalendarConstraints.DateValidator {

    private var now = LocalDate.now()

    constructor(parcel: Parcel) : this()

    override fun isValid(dateLong: Long): Boolean {
        val date = LocalDateTime.ofEpochSecond(
            dateLong / 1000, 0, ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            )
        )

        val startYear = if (now.monthValue <= 6) now.year - 1 else now.year
        val startOfSchoolYear = now.withYear(startYear).firstSchoolDay
        val endYear = if (now.monthValue > 6) now.year + 1 else now.year
        val endOfSchoolYear = now.withYear(endYear).lastSchoolDay

        return date.toLocalDate().until(endOfSchoolYear, ChronoUnit.DAYS) >= 0 && date.toLocalDate()
            .until(startOfSchoolYear, ChronoUnit.DAYS) <= 0 && date.dayOfWeek != DayOfWeek.SUNDAY
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun hashCode(): Int {
        val hashedFields = arrayOf<Any>()
        return hashedFields.contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SchoolDaysValidator

        if (now != other.now) return false

        return true
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    companion object CREATOR : Parcelable.Creator<SchoolDaysValidator> {
        override fun createFromParcel(parcel: Parcel): SchoolDaysValidator {
            return SchoolDaysValidator(parcel)
        }

        override fun newArray(size: Int): Array<SchoolDaysValidator?> {
            return arrayOfNulls(size)
        }
    }
}