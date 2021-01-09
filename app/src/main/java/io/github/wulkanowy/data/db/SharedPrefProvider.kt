package io.github.wulkanowy.data.db

import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.wulkanowy.utils.toLocalDateTime
import io.github.wulkanowy.utils.toTimestamp
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefProvider @Inject constructor(private val sharedPref: SharedPreferences) {

    companion object {
        const val APP_VERSION_CODE_KEY = "app_version_code"
    }

    fun putLong(key: String, value: Long, sync: Boolean = false) {
        sharedPref.edit(sync) { putLong(key, value) }
    }

    fun getLong(key: String, defaultValue: Long) = sharedPref.getLong(key, defaultValue)

    fun getString(key: String, defaultValue: String): String = sharedPref.getString(key, defaultValue) ?: defaultValue

    fun putString(key: String, value: String, sync: Boolean = false) {
        sharedPref.edit(sync) { putString(key, value) }
    }

    fun delete(key: String) {
        sharedPref.edit().remove(key).apply()
    }

    fun isShouldBeRefreshed(key: String): Boolean {
        val timestamp = getLong(key, 0).toLocalDateTime()

        val shouldBeRefreshed = timestamp < LocalDateTime.now().minusSeconds(60)

        Timber.d("Check if $key need to be refreshed: $shouldBeRefreshed (last refresh: $timestamp)")

        return shouldBeRefreshed
    }

    fun updateLastRefreshTimestamp(key: String) {
        putLong(key, LocalDateTime.now().toTimestamp())
    }
}
