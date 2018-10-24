package io.github.wulkanowy.ui.modules.settings

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.ServiceRepository
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    private val preferencesRepository: PreferencesRepository,
    private val serviceRepository: ServiceRepository
) : BasePresenter<SettingsView>(errorHandler) {

    fun onSharedPreferenceChanged(key: String) {

        when (key) {
            preferencesRepository.serviceEnablesKey,
            preferencesRepository.servicesIntervalKey,
            preferencesRepository.servicesOnlyWifiKey -> {
                serviceRepository.reloadFullSyncService()
            }
        }
    }
}
