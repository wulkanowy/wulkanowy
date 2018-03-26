package io.github.wulkanowy.ui.main.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import io.github.wulkanowy.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
