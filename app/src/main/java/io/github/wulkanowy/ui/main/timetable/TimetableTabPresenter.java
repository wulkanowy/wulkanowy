package io.github.wulkanowy.ui.main.timetable;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;

public class TimetableTabPresenter extends BasePresenter<TimetableTabContract.View>
        implements TimetableTabContract.Presenter {

    private String date;

    @Inject
    TimetableTabPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        if (isVisible) {
            getView().setTestText(date);
        }
    }

    @Override
    public void setArgumentDate(String date) {
        this.date = date;
    }
}
