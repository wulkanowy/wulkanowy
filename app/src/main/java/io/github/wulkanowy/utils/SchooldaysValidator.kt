package io.github.wulkanowy.utils

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.time.LocalDate

class SchoolDaysValidator() : CalendarConstraints.DateValidator {

    private var now = LocalDate.now()

    constructor(parcel: Parcel) : this() {
    }

    override fun isValid(dateLong: Long): Boolean {
        val date = LocalDate.ofEpochDay(dateLong/3600000/24)

        val startYear = if (now.monthValue <= 6) now.year - 1 else now.year
        val startOfSchoolYear = now.withYear(startYear).firstSchoolDay
        val endYear = if (now.monthValue > 6) now.year + 1 else now.year
        val endOfSchoolYear = now.withYear(endYear).lastSchoolDay

        return date.year >= startOfSchoolYear.year && date.year <= endOfSchoolYear.year &&
            (date.monthValue >= startOfSchoolYear.monthValue-1 || date.monthValue <= endOfSchoolYear.monthValue-1) &&
            date.dayOfMonth >= startOfSchoolYear.dayOfMonth && date.dayOfMonth <= endOfSchoolYear.dayOfMonth
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