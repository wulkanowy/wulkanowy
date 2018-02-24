package io.github.wulkanowy.ui.main.timetable;

import io.github.wulkanowy.ui.base.BaseContract;

public interface TimetableTabContract {

    interface View extends BaseContract.View {

    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentVisible(boolean isVisible);

        void setArgumentDate(String date);

        void onStart(View view, boolean primary);
    }
}
