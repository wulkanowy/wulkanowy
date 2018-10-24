package io.github.wulkanowy.data.repositories

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(private val sharedPref: SharedPreferences) {

    companion object {
        const val KEY_SERVICES_ENABLE = "services_enable"
        const val KEY_SERVICES_INTERVAL = "services_interval"
        const val KEY_SERVICES_WIFI_ONLY = "services_disable_wifi_only"
        const val KEY_NOTIFICATIONS_ENABLE = "notifications_enable"
    }

    val startMenuIndex: Int
        get() = sharedPref.getString("start_menu", "0")?.toInt() ?: 0

    val showPresent: Boolean
        get() = sharedPref.getBoolean("attendance_present", true)

    val serviceEnables: Boolean
        get() = sharedPref.getBoolean(KEY_SERVICES_ENABLE, true)

    val servicesInterval: Int
        get() = sharedPref.getString(KEY_SERVICES_INTERVAL, "60")?.toInt() ?: 60

    val servicesOnlyWifi: Boolean
        get() = sharedPref.getBoolean(KEY_SERVICES_WIFI_ONLY, true)

    val notificationsEnable: Boolean
        get() = sharedPref.getBoolean(KEY_NOTIFICATIONS_ENABLE, true)
}
