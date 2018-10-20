package io.github.wulkanowy.ui.main.settings

import android.os.Bundle
import com.mikepenz.aboutlibraries.LibsBuilder
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.main.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.scheme_preferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findPreference("version").summary = VERSION_NAME
        findPreference("libraries").setOnPreferenceClickListener {
            (activity as? MainActivity)?.pushFragment(LibsBuilder().supportFragment())
            true
        }

    }
}

