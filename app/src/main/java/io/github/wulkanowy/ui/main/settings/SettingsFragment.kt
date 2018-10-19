package io.github.wulkanowy.ui.main.settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.mikepenz.aboutlibraries.LibsBuilder
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.main.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val PREF_KEY_DEFAULT_VIEW = "startup_menu"

        const val PREF_KEY_SHOW_PRESENT = "attendance_present"

        fun newInstance() = SettingsFragment()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, key: String?) {
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

