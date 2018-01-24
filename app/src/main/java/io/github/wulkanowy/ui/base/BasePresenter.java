package io.github.wulkanowy.ui.base;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;

public class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {

    private final DatabaseManager databaseManager;

    private V view;

    @Inject
    public BasePresenter(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void onStart(@NonNull V activity) {
        view = activity;
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    public final DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public V getView() {
        return view;
    }
}
