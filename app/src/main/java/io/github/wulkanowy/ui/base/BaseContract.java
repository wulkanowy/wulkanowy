package io.github.wulkanowy.ui.base;

import android.support.annotation.StringRes;

public interface BaseContract {

    interface View {

        void onError(@StringRes int resId);

        void onError(String message);

        void onNoNetworkError();

        boolean isNetworkConnected();
    }

    interface Presenter<V extends View> {

        void onStart(V view);

        void onDestroy();
    }
}
