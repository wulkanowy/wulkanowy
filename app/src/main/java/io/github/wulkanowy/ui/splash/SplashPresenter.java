package io.github.wulkanowy.ui.splash;

import android.os.Handler;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.ui.base.BasePresenter;

public class SplashPresenter extends BasePresenter<SplashActivity> {

    @Inject
    public SplashPresenter(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    @Override
    public void onStart(@NonNull SplashActivity activity) {
        super.onStart(activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getDatabaseManager().getCurrentUserId() == 0) {
                    getConnectedActivity().openLoginActivity();
                } else {
                    getConnectedActivity().startFullSyncService();
                    getConnectedActivity().openDashboardActivity();
                }
            }
        }, 500);
    }
}
