package io.github.wulkanowy.data.db.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.SharedPreferencesInfo;

@Singleton
public class SharedPref implements SharedPrefContract {

    private static final String SHARED_KEY_USER_ID = "USER_ID";

    private static final String SHARED_KEY_START_TAB = "startup_tab";

    private final SharedPreferences appSharedPref;

    private final SharedPreferences settingsSharedPref;

    @Inject
    SharedPref(@ApplicationContext Context context, @SharedPreferencesInfo String sharedName) {
        appSharedPref = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        settingsSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public long getCurrentUserId() {
        return appSharedPref.getLong(SHARED_KEY_USER_ID, 0);
    }

    @Override
    public void setCurrentUserId(long userId) {
        appSharedPref.edit().putLong(SHARED_KEY_USER_ID, userId).apply();
    }

    @Override
    public int getStartupTab() {
        return Integer.valueOf(settingsSharedPref.getString(SHARED_KEY_START_TAB, "2"));
    }
}
