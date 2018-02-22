package io.github.wulkanowy.ui.main.timetable;

import io.github.wulkanowy.ui.base.BaseContract;

public interface TimetableTabContract {

    interface View extends BaseContract.View {

        void setTestText(String message);

        void setPageSelected(boolean isSelected);
    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentVisible(boolean isVisible);

        void onFragmentVisiblePrimary(boolean isPrimary);

        void setArgumentDate(String date);
    }
}
