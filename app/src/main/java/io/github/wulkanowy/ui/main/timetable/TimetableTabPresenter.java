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

    @Inject
    TimetableTabPresenter(RepositoryContract repository, MutableBoolean isFirstRun) {
        super(repository);
        this.isFirstRun = isFirstRun;
    }

    @Override
    public void onViewCreated() {
        onFragmentVisible(getView().getUserVisibleHint(), true);
    }

    @Override
    public void onFragmentVisible(boolean isVisible, boolean isResumed) {
        if (!isFirstSight && isVisible && isResumed && isFirstRun.isFalse()) {
            getView().setTestText(date);
            LogUtils.debug(date);
            isFirstSight = true;
        }

        if (isFirstRun.isTrue()) {
            isFirstRun.setFalse();
        }
    }

    @Override
    public void setArgumentDate(String date) {
        this.date = date;
    }
}
