package io.github.wulkanowy.ui.main.settings;

import android.os.Bundle;

import io.github.wulkanowy.R;

public class AboutScreen extends SettingsFragment {

    public AboutScreen() {
        // silence is golden
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
