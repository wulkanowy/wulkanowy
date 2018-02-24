package io.github.wulkanowy.ui.main.timetable;


import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;

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
            isFirstSight = true;

            //   List<Day> dayList = getRepository().getWeek(date).getDayList();
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
