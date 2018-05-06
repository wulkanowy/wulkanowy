package io.github.wulkanowy.ui.main.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import io.github.wulkanowy.R;
import io.github.wulkanowy.services.jobs.SyncJob;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String SHARED_KEY_START_TAB = "startup_tab";

    public static final String SHARED_KEY_GRADES_SUMMARY = "grades_summary";

    public static final String SHARED_KEY_ATTENDANCE_PRESENT = "attendance_present";

    public static final String SHARED_KEY_SERVICES_ENABLE = "services_enable";

    public static final String SHARED_KEY_NOTIFY_ENABLE = "notify_enable";

    public static final String SHARED_KEY_SERVICES_INTERVAL = "services_interval";

    public static final String SHARED_KEY_SERVICES_MOBILE_DISABLED = "services_disable_mobile";

    public static final String SHARED_KEY_ABOUT_OSL = "about_osl";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findPreference(SHARED_KEY_ABOUT_OSL).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), OssLicensesMenuActivity.class);
                String title = getString(R.string.pref_about_osl);
                intent.putExtra("title", title);
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SHARED_KEY_SERVICES_ENABLE) || key.equals(SHARED_KEY_SERVICES_INTERVAL)
                || key.equals(SHARED_KEY_SERVICES_MOBILE_DISABLED)) {
            launchServices(sharedPreferences.getBoolean(SHARED_KEY_SERVICES_ENABLE, true),
                    sharedPreferences);
        }
    }

    private void launchServices(boolean start, SharedPreferences sharedPref) {
        if (start) {
            int newInterval = Integer.parseInt(sharedPref.getString(SHARED_KEY_SERVICES_INTERVAL, "60"));
            boolean useOnlyWifi = sharedPref.getBoolean(SHARED_KEY_SERVICES_MOBILE_DISABLED, false);

            SyncJob.stop(getContext());
            SyncJob.start(getContext(), newInterval, useOnlyWifi);
        } else {
            SyncJob.stop(getContext());
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            getActivity().setTitle(R.string.settings_text);
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
