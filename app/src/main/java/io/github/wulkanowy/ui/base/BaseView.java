package io.github.wulkanowy.ui.base;

import android.support.annotation.StringRes;

public interface BaseView {

    void showLoadingBar();

    void hideLoadingBar();

    void onError(@StringRes int resId);

    void onError(String message);
}
