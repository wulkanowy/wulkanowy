package io.github.wulkanowy.ui.login;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface LoginContract {
    interface View extends BaseContract.View {

        void requestPasswordViewFocus();

        void requestEmailViewFocus();

        void setPasswordError(String message);

        void setEmailError(String message);

        void resetViewErrors();

        void hideSoftKeyboard();

    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void attemptLogin(String email, String password, String symbol);
    }
}
