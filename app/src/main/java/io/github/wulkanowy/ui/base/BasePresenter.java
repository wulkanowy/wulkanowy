package io.github.wulkanowy.ui.base;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;

public class BasePresenter implements RootPresenter {

    private final DatabaseManager databaseManager;

    private RootActivity rootActivity;

    @Inject
    public BasePresenter(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void onStart(@NonNull RootActivity rootActivity) {
        this.rootActivity = rootActivity;
    }

    @Override
    public void onDestroy() {
        rootActivity = null;
    }

    public final DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
