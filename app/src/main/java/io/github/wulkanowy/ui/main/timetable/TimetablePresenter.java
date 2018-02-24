package io.github.wulkanowy.ui.main.timetable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.TimeUtils;

public class TimetablePresenter extends BasePresenter<TimetableContract.View>
        implements TimetableContract.Presenter {

    private List<String> dates = new ArrayList<>();

    private int positionToScroll;

    private boolean isFirstSight = false;

    @Inject
    TimetablePresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(TimetableContract.View view, boolean primary) {
        super.onStart(view);

        if (dates.isEmpty()) {
            dates = TimeUtils.getMondaysFromCurrentSchoolYear();
        }
        positionToScroll = dates.indexOf(TimeUtils.getDateOfCurrentMonday());

        if (primary) {
            onFragmentVisible(true);
        }
    }

    @Override
    public void onFragmentVisible(boolean isSelected) {
        if (isSelected) {
            getView().setActivityTitle();

            if (!isFirstSight) {
                isFirstSight = true;

                for (String date : dates) {
                    getView().addPageToAdapter(TimetableTabFragment.newInstance(date), date);
                }
                getView().setAdapterWithTabLayout();
                getView().scrollViewPagerToPosition(positionToScroll);
            }
        }
    }

    @Override
    public void onTabSelected(int position) {
        getView().setChildFragmentSelected(position, true);
    }

    @Override
    public void onTabUnselected(int position) {
        getView().setChildFragmentSelected(position, false);
    }
}
