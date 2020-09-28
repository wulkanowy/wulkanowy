package io.github.wulkanowy.data.db

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.util.Date

class Converters {

    private val moshi by lazy { Moshi.Builder().build() }

    private val integerListAdapter by lazy {
        moshi.adapter<List<Int>>(Types.newParameterizedType(List::class.java, Integer::class.java))
    }

    private val stringListMapAdapter by lazy {
        moshi.adapter<List<Map<String, String>>>(Types.newParameterizedType(List::class.java, Map::class.java, String::class.java))
    }

    @TypeConverter
    fun timestampToDate(value: Long?): LocalDate? = value?.run {
        Date(value).toInstant().atZone(ZoneOffset.UTC).toLocalDate()
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }

    @TypeConverter
    fun timestampToTime(value: Long?): LocalDateTime? = value?.let {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC)
    }

    @TypeConverter
    fun timeToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun monthToInt(month: Month?) = month?.value

    @TypeConverter
    fun intToMonth(value: Int?) = value?.let { Month.of(it) }

    @TypeConverter
    fun intListToJson(list: List<Int>): String {
        return integerListAdapter.toJson(list)
    }

    @TypeConverter
    fun jsonToIntList(value: String): List<Int> {
        return integerListAdapter.fromJson(value).orEmpty()
    }

    @TypeConverter
    fun stringPairListToJson(list: List<Pair<String, String>>): String {
        return stringListMapAdapter.toJson(list.map { mapOf(it) })
    }

    @TypeConverter
    fun jsonToStringPairList(value: String): List<Pair<String, String>> {
        if (value.startsWith("{")) {
            Timber.w("Malformed json: Expected BEGIN_ARRAY but was BEGIN_OBJECT at path \$")
            return emptyList()
        }

        return stringListMapAdapter.fromJson(value)?.flatMap { map -> map.map { it.key to it.value } }.orEmpty()
    }
}
