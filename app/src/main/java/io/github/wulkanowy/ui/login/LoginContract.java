package io.github.wulkanowy.ui.login;

import android.support.annotation.StringRes;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface LoginContract {
    interface View extends BaseContract.View {

        void requestPasswordViewFocus();

        void requestEmailViewFocus();

        void setPasswordError(@StringRes int resId);

        void setEmailError(@StringRes int resId);

        void resetViewErrors();

    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void attemptLogin(String email, String password, String symbol);

        void openInternalBrowserViewer(String url);
    }
}
