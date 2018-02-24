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

        void setChildFragmentSelected(int position, boolean selected);
    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentVisible(boolean isSelected);

        void onTabSelected(int position);

        void onTabUnselected(int position);

        void onStart(View view, boolean primary);
    }
}
