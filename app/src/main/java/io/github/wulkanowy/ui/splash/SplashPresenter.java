package io.github.wulkanowy.ui.splash;

import android.os.Handler;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.ui.base.BasePresenter;

public class SplashPresenter extends BasePresenter<SplashContract.View>
        implements SplashContract.Presenter {

    @Inject
    public SplashPresenter(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    @Override
    public void onStart(@NonNull SplashContract.View activity) {
        super.onStart(activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getDatabaseManager().getCurrentUserId() != 0) {
                    getView().openLoginActivity();
                } else {
                    getView().startFullSyncService();
                    getView().openMainActivity();
                }
            }
        }, 500);
    }
}
