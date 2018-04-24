package io.github.wulkanowy.ui.main.exams;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public interface ExamsContract {

    interface View extends BaseContract.View {

        void setActivityTitle();

        boolean isMenuVisible();
    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void onStart(View view, OnFragmentIsReadyListener listener);

        void onFragmentVisible(boolean isVisible);
    }
}
