package io.github.wulkanowy.data.db

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefHelper @Inject constructor(private val sharedPref: SharedPreferences) {

    fun putLong(key: String, value: Long) {
        sharedPref.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        sharedPref.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPref.getInt(key, defaultValue)
    }
}