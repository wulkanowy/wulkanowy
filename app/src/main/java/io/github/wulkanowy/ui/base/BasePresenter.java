package io.github.wulkanowy.ui.base;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;

public class BasePresenter<V extends BaseActivity> {

    private final DatabaseManager databaseManager;

    private V view;

    @Inject
    public BasePresenter(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void onStart(@NonNull V activity) {
        view = activity;
    }

    public void onDestroy() {
        view = null;
    }

    public final DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public V getConnectedActivity() {
        return view;
    }
}
