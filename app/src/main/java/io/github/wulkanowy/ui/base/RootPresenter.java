package io.github.wulkanowy.ui.base;

import android.support.annotation.NonNull;

public interface RootPresenter {

    void onStart(@NonNull RootActivity rootActivity);

    void onDestroy();
}
