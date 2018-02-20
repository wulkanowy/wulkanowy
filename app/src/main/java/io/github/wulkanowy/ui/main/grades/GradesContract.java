package io.github.wulkanowy.ui.main.grades;

import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface GradesContract {

    interface View extends BaseContract.View, SwipeRefreshLayout.OnRefreshListener {

        void showProgressBar(boolean show);

        void updateAdapterList(List<GradeHeaderItem> headerItems);

        void showNoItem(boolean show);

        void onNoNetworkError();

        void onRefreshSuccessNoGrade();

        void onRefreshSuccess(int number);

        void hideRefreshingBar();

        void setActivityTitle();

    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentVisible(boolean isVisible);

        void onRefresh();

        void onCanceledAsync();

        void onEndAsync(boolean success, Exception exception);

        RepositoryContract getRepository();
    }
}
