package io.github.wulkanowy.ui.main.timetable;

import io.github.wulkanowy.ui.base.BaseContract;

public interface TimetableTabContract {

    interface View extends BaseContract.View {

        void setTestText(String message);

        boolean getUserVisibleHint();
    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentVisible(boolean isVisible, boolean isResumed);

        void onViewCreated();

        void setArgumentDate(String date);
    }
}
