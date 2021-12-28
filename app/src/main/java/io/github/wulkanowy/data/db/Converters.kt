package io.github.wulkanowy.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.*
import java.util.*

class Converters {

    private val json = Json

    @TypeConverter
    fun timestampToDate(value: Long?): LocalDate? =
        value?.let(::Date)?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate()

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
    fun instantToTimestamp(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun timestampToInstant(timestamp: Long?): Instant? = timestamp?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun zonedDateTimeToTimestamp(zonedDateTime: ZonedDateTime?): Long? =
        zonedDateTime?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun timestampToZonedDateTime(timestamp: Long?): ZonedDateTime? =
        timestampToInstant(timestamp)?.atZone(ZoneId.systemDefault())

    @TypeConverter
    fun monthToInt(month: Month?) = month?.value

    @TypeConverter
    fun intToMonth(value: Int?) = value?.let { Month.of(it) }

    @TypeConverter
    fun intListToJson(list: List<Int>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun jsonToIntList(value: String): List<Int> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun stringPairListToJson(list: List<Pair<String, String>>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun jsonToStringPairList(value: String): List<Pair<String, String>> {
        return try {
            json.decodeFromString(value)
        } catch (e: SerializationException) {
            emptyList() // handle errors from old gson Pair serialized data
        }
    }
}
