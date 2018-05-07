package io.github.wulkanowy.ui.main.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import io.github.wulkanowy.BuildConfig;

public class VersionPreference extends Preference {

    public VersionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttached() {
        setSummary(BuildConfig.VERSION_NAME);
    }
}
