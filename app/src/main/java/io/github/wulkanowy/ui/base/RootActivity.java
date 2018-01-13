package io.github.wulkanowy.ui.base;

import android.support.annotation.StringRes;

public interface RootActivity {

    void showLoadingBar();

    void hideLoadingBar();

    void onError(@StringRes int resId);

    void onError(String message);

    void isNetworkConnected();
}
