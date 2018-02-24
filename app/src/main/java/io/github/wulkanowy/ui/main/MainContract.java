package io.github.wulkanowy.ui.main;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface MainContract {

    interface View extends BaseContract.View {

        void setCurrentPage(int position);

        void setChildFragmentSelected(int position, boolean selected);
    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void onTabSelected(int position, boolean wasSelected, int defaultPosition);

    }
}
