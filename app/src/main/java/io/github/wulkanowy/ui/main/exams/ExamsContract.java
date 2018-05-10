package io.github.wulkanowy.ui.main.exams;

import io.github.wulkanowy.ui.base.BaseContract;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public interface ExamsContract {

    interface View extends BaseContract.View {

        void setActivityTitle();

        boolean isMenuVisible();

        void scrollViewPagerToPosition(int position);

        void setTabDataToAdapter(String date);

        void setAdapterWithTabLayout();

        void setThemeForTab(int position);
    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onStart(View view, OnFragmentIsReadyListener listener);

        void onFragmentActivated(boolean isVisible);

        void setRestoredPosition(int position);
    }
}
