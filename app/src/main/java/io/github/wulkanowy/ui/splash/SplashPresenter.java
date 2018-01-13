package io.github.wulkanowy.ui.splash;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.ui.base.RootActivity;

public class SplashPresenter extends BasePresenter {

    @Inject
    public SplashPresenter(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    @Override
    public void onStart(@NonNull RootActivity rootActivity) {
        super.onStart(rootActivity);
    }
}
