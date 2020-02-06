package io.github.wulkanowy.ui.modules.settings

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import com.readystatesoftware.chuck.api.ChuckCollector
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.isHolidays
import org.threeten.bp.LocalDate.now
import timber.log.Timber
import javax.inject.Inject

const val cSyncPermissionRequest = 0

class SettingsPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: FirebaseAnalyticsHelper,
    private val syncManager: SyncManager,
    private val chuckCollector: ChuckCollector,
    private val appInfo: AppInfo,
    private val context: Context
) : BasePresenter<SettingsView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: SettingsView) {
        super.onAttachView(view)
        Timber.i("Settings view was initialized")
        view.setServicesSuspended(preferencesRepository.serviceEnableKey, now().isHolidays)
        view.initView()
    }

    fun onSharedPreferenceChanged(key: String) {
        Timber.i("Change settings $key")

        with(preferencesRepository) {
            when (key) {
                serviceEnableKey -> with(syncManager) { if (isServiceEnabled) startSyncWorker() else stopSyncWorker() }
                servicesIntervalKey, servicesOnlyWifiKey -> syncManager.startSyncWorker(true)
                isDebugNotificationEnableKey -> chuckCollector.showNotification(isDebugNotificationEnable)
                appThemeKey -> view?.recreateView()
                appLanguageKey -> view?.run {
                    updateLanguage(if (appLanguage == "system") appInfo.systemLanguage else appLanguage)
                    recreateView()
                }
                calendarSyncEnableKey -> view?.checkPermission()
                else -> Unit
            }
        }
        analytics.logEvent("setting_changed", "name" to key)
    }

    fun onCalendarSyncSelectClick(preference: ListPreference){
        val fields = arrayOf(
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME
        )
        var entries = emptyArray<String>()
        var entryValues = emptyArray<String>()
        val cursor = context.contentResolver.query(Uri.parse("content://com.android.calendar/calendars"), fields, null, null, null)
        if (cursor != null) {
            if(cursor.count > 0) {
                while (cursor.moveToNext()){
                    entries += cursor.getString(0)
                    entryValues += cursor.getString(1)
                }
                preference.apply {
                    setEntries(entries)
                    setEntryValues(entryValues)
                }
            }
        } else {

        }

    }

}
