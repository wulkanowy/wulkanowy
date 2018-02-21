package io.github.wulkanowy.ui.main.timetable;


import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.LogUtils;

public class TimetableTabPresenter extends BasePresenter<TimetableTabContract.View>
        implements TimetableTabContract.Presenter {

    private String date;

    private MutableBoolean isFirstRun;

    private boolean isFirstSight = false;

    private boolean isFragmentVisible = false;

    @Inject
    TimetableTabPresenter(RepositoryContract repository, MutableBoolean isFirstRun) {
        super(repository);
        this.isFirstRun = isFirstRun;
    }


    @Override
    public void onResumeFragment() {
        if (!isFirstSight && isFragmentVisible && isFirstRun.isFalse()) {
            getView().setTestText(date);
            LogUtils.debug(date);
            isFirstSight = true;
        }

        if (isFirstRun.isTrue() && isFragmentVisible) {
            isFirstRun.setFalse();
        }
    }

    @Override
    public void setArgumentDate(String date) {
        this.date = date;
    }

    @Override
    public void setFragmentVisible(boolean isVisible) {
        isFragmentVisible = isVisible;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFirstSight = false;
        isFragmentVisible = false;
    }
}
