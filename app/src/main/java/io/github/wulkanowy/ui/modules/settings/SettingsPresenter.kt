package io.github.wulkanowy.ui.modules.settings

import android.content.SharedPreferences
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.ServiceRepository
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    private val serviceRepository: ServiceRepository
) : BasePresenter<SettingsView>(errorHandler) {

    fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            PreferencesRepository.KEY_SERVICES_ENABLE,
            PreferencesRepository.KEY_SERVICES_INTERVAL,
            PreferencesRepository.KEY_SERVICES_WIFI_ONLY -> {
                serviceRepository.startFullSyncService()
                view?.showMessage("Service restarted")
            }
        }
    }
}
