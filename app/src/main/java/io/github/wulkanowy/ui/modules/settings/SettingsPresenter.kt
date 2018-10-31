package io.github.wulkanowy.ui.modules.settings

import android.content.SharedPreferences
import io.github.wulkanowy.BuildConfig
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.job.ServiceHelper
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    private val preferencesRepository: PreferencesRepository,
    private val serviceHelper: ServiceHelper
) : BasePresenter<SettingsView>(errorHandler) {

    fun onSharedPreferenceChanged(sharedPref: SharedPreferences, key: String) {
        when (key) {
            preferencesRepository.serviceEnablesKey -> {
                if (sharedPref.getBoolean(preferencesRepository.serviceEnablesKey, true))
                    serviceHelper.startFullSyncService()
                else serviceHelper.stopFullSyncService()
            }
            preferencesRepository.servicesIntervalKey,
            preferencesRepository.servicesOnlyWifiKey -> {
                serviceHelper.reloadFullSyncService()
                if (BuildConfig.DEBUG) view?.showMessage("Services reloaded")
            }
            preferencesRepository.currentThemeKey -> {
                view?.setTheme(sharedPref.getString(preferencesRepository.currentThemeKey, "1")?.toInt() ?: 1)
            }
        }
    }
}
