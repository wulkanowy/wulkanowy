package io.github.wulkanowy.ui.splash;

import io.github.wulkanowy.ui.base.BaseContract;

public interface SplashContract {

    interface View extends BaseContract.View {

        void openLoginActivity();

        void openMainActivity();

        void cancelNotifications();
    }

    interface Presenter extends BaseContract.Presenter<View> {
    }
}
