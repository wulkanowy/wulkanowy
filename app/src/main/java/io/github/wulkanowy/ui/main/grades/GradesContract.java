package io.github.wulkanowy.ui.main.grades;

import java.util.List;

import io.github.wulkanowy.ui.base.BaseContract;

public interface GradesContract {

    interface View extends BaseContract.View {

        void showProgressBar(boolean show);

        void updateAdapterList(List<GradeHeaderItem> headerItems);

    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentVisible(boolean isVisible);
    }
}
