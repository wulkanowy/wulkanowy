package io.github.wulkanowy.db.shared;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.SharedPreferencesInfo;

@Singleton
public class SharedHelper {

    private static final String SHARED_KEY_USER_ID = "USER_ID";

    private final SharedPreferences sharedPreferences;

    @Inject
    public SharedHelper(@ApplicationContext Context context, @SharedPreferencesInfo String sharedName) {
        sharedPreferences = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
    }

    public long getCurrentUserId() {
        return sharedPreferences.getLong(SHARED_KEY_USER_ID, 0);
    }

    public void setCurrentUserId(long userId) {
        sharedPreferences.edit().putLong(SHARED_KEY_USER_ID, userId).apply();
    }
}
