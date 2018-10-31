package io.github.wulkanowy.ui.modules.settings

import android.content.SharedPreferences
import io.github.wulkanowy.BuildConfig
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.job.ServiceHelper
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.isHolidays
import org.threeten.bp.LocalDate.now
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    private val preferencesRepository: PreferencesRepository,
    private val serviceHelper: ServiceHelper
) : BasePresenter<SettingsView>(errorHandler) {

    override fun onAttachView(view: SettingsView) {
        super.onAttachView(view)

        view.run {
            setServicesSuspended(preferencesRepository.serviceEnablesKey, now().isHolidays)
        }
    }

    fun onSharedPreferenceChanged(sharedPref: SharedPreferences, key: String) {
        when (key) {
            preferencesRepository.serviceEnablesKey -> {
                if (sharedPref.getBoolean(preferencesRepository.serviceEnablesKey, true)) {
                    serviceHelper.startFullSyncService()
                    if (BuildConfig.DEBUG) view?.showMessage("Services started")
                } else {
                    serviceHelper.stopFullSyncService()
                    if (BuildConfig.DEBUG) view?.showMessage("Services stopped")
                }
            }
            preferencesRepository.servicesIntervalKey,
            preferencesRepository.servicesOnlyWifiKey -> {
                serviceHelper.reloadFullSyncService()
                if (BuildConfig.DEBUG) view?.showMessage("Services reloaded")
            }
            preferencesRepository.currentThemeKey -> {
                val themeVal = sharedPref.getString(preferencesRepository.currentThemeKey, "1")?.toInt() ?: 1
                view?.setTheme(themeVal)
                if (BuildConfig.DEBUG) view?.showMessage("Theme changed to: $themeVal")
            }
        }
    }
}
