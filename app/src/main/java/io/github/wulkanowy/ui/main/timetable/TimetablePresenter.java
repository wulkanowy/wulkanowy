package io.github.wulkanowy.ui.main.timetable;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.TimeUtils;

public class TimetablePresenter extends BasePresenter<TimetableContract.View>
        implements TimetableContract.Presenter {

    private List<String> dates = new ArrayList<>();

    @Inject
    TimetablePresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(@NonNull TimetableContract.View view) {
        super.onStart(view);

        if (dates.isEmpty()) {
            dates = TimeUtils.getMondaysFromCurrentSchoolYear();
        }

        // getView().scrollViewPagerToPosition(dates.indexOf(TimeUtils.getDateOfCurrentMonday()));
    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        if (isVisible) {
            getView().setActivityTitle();

            for (String date : dates) {
                getView().addPageToAdapter(TimetableTabFragment.newInstance(date), date);
            }

            getView().setAdapterWithTabLayout();
        }
    }
}
