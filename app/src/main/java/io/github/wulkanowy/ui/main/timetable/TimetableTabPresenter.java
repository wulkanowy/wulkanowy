package io.github.wulkanowy.ui.main.timetable;


import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.LogUtils;

public class TimetableTabPresenter extends BasePresenter<TimetableTabContract.View>
        implements TimetableTabContract.Presenter {

    private String date;

    private boolean isFirstSight = false;

    @Inject
    TimetableTabPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(TimetableTabContract.View view, boolean primary) {
        super.onStart(view);
        if (primary) {
            onFragmentVisible(true);
        }
    }

    @Override
    public void onFragmentVisible(boolean selected) {
        if (!isFirstSight && selected) {
            getView().setTestText(date);
            LogUtils.debug(date);
            isFirstSight = true;
        }
    }

    @Override
    public void setArgumentDate(String date) {
        this.date = date;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFirstSight = false;
    }
}
