package io.github.wulkanowy.ui.main.timetable;

import io.github.wulkanowy.ui.base.BaseContract;

public interface TimetableTabContract {

    interface View extends BaseContract.View {

        void setTestText(String message);
    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentVisible(boolean isVisible);

        void setArgumentDate(String date);
    }
}
