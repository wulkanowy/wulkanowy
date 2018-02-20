package io.github.wulkanowy.ui.main.timetable;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface TimetableContract {

    interface View extends BaseContract.View {

        void setActivityTitle();

        void showProgressBar(boolean show);

        void scrollViewPagerToPosition(int position);

        void addPageToAdapter(TimetableTabFragment fragment, String title);

        void setAdapterWithTabLayout();
    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentVisible(boolean isVisible);
    }
}
