package io.github.wulkanowy.utils

import android.os.Parcel
import android.os.Parcelable
import com.wdullaer.materialdatetimepicker.date.DateRangeLimiter
import org.threeten.bp.LocalDate
import java.util.Calendar

@Suppress("UNUSED_PARAMETER")
class SchooldaysRangeLimiter() : DateRangeLimiter {
    var year: Int = LocalDate.now().year

    constructor(year: Int) : this() {
        this.year = year
    }

    constructor(parcel: Parcel) : this()

    override fun setToNearestDate(day: Calendar): Calendar {
        return day
    }

    override fun isOutOfRange(year: Int, month: Int, day: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY
    }

    override fun getStartDate(): Calendar {
        val output = Calendar.getInstance()
        output.set(Calendar.YEAR, year)
        output.set(Calendar.DAY_OF_MONTH, 1)
        output.set(Calendar.MONTH, Calendar.JANUARY)
        return output
    }

    override fun getEndDate(): Calendar {
        val output = Calendar.getInstance()
        output.set(Calendar.YEAR, year)
        output.set(Calendar.DAY_OF_MONTH, 31)
        output.set(Calendar.MONTH, Calendar.DECEMBER)
        return output
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SchooldaysRangeLimiter> {
        override fun createFromParcel(parcel: Parcel): SchooldaysRangeLimiter {
            return SchooldaysRangeLimiter(parcel)
        }

        override fun newArray(size: Int): Array<SchooldaysRangeLimiter?> {
            return arrayOfNulls(size)
        }
    }
}