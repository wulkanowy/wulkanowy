package io.github.wulkanowy.ui.main.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

public class VersionPreference extends Preference {

    public VersionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttached() {
        String version;
        try {
            version = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "undefined";
        }

        setSummary(version);
    }
}
