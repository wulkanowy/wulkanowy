package io.github.wulkanowy.ui.main.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import io.github.wulkanowy.R;
import io.github.wulkanowy.services.SyncJob;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String SHARED_KEY_START_TAB = "startup_tab";

    public static final String SHARED_KEY_SERVICES_ENABLE = "services_enable";

    public static final String SHARED_KEY_NOTIFY_ENABLE = "notify_enable";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SHARED_KEY_SERVICES_ENABLE)) {
            if (sharedPreferences.getBoolean(SHARED_KEY_SERVICES_ENABLE, true)) {
                SyncJob.start(getContext());
            } else {
                SyncJob.stop(getContext());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
