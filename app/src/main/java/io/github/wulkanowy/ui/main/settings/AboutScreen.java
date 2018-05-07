package io.github.wulkanowy.ui.main.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import io.github.wulkanowy.R;

public class AboutScreen extends SettingsFragment {

    public AboutScreen() {
        // silence is golden
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
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
