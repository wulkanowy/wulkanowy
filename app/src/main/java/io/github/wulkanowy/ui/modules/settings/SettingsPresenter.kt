package io.github.wulkanowy.ui.modules.settings

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import timber.log.Timber
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val analytics: AnalyticsHelper,
) : BasePresenter<SettingsView>(errorHandler, studentRepository) {

    override fun onAttachView(view: SettingsView) {
        super.onAttachView(view)
        Timber.i("Settings view was initialized")
        view.initView()
    }

    fun onSharedPreferenceChanged(key: String) {
        Timber.i("Change settings $key")
        analytics.logEvent("setting_changed", "name" to key)
    }
}
