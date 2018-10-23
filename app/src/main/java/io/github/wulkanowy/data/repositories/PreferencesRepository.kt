package io.github.wulkanowy.data.repositories

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(private val sharedPref: SharedPreferences) {

    val startMenuIndex: Int
        get() = sharedPref.getString("start_menu", "0")?.toInt() ?: 0

    val showPresent: Boolean
        get() = sharedPref.getBoolean("attendance_present", true)

    val serviceEnables: Boolean
        get() = sharedPref.getBoolean("services_enable", true)

    val servicesInterval: Long
        get() = sharedPref.getString("services_interval", "60")?.toLong() ?: 60L

    val servicesOnlyWifi: Boolean
        get() = sharedPref.getBoolean("services_disable_mobile", true)

    val notificationsEnable: Boolean
        get() = sharedPref.getBoolean("notifications_enable", true)
}

