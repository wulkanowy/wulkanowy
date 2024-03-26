package io.github.wulkanowy.ui.modules.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AdsHelper
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), MainView.TitledView, SettingsView {

    companion object {

        fun newInstance() = SettingsFragment()
    }

    @Inject
    lateinit var adsHelper: AdsHelper

    override val titleStringId get() = R.string.settings_title

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.scheme_preferences, rootKey)
        findPreference<Preference>("ads")?.isVisible = adsHelper.supportsAds
        Timber.i("Settings view was initialized")
    }

    override fun showError(text: String, error: Throwable) {}

    override fun showMessage(text: String) {}

    override fun showExpiredCredentialsDialog() {}

    override fun onCaptchaVerificationRequired(url: String?) = Unit

    override fun showDecryptionFailedDialog() {}

    override fun openClearLoginView() {}

    override fun showErrorDetailsDialog(error: Throwable) {}

    override fun showChangePasswordSnackbar(redirectUrl: String) {}

    override fun showAuthDialog() {}
}
