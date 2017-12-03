package io.github.wulkanowy.activity.dashboard.timetable;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.AbstractFragment;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.dao.entities.Day;
import io.github.wulkanowy.dao.entities.Lesson;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;

public class TimetableFragment extends AbstractFragment<TimetableHeaderItem> {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_timetable;
    }

    @Override
    public int getRecyclerViewId() {
        return R.id.timetable_recycler;
    }

    @Override
    public int getRefreshLayoutId() {
        return R.id.timetable_refresh_layout;
    }

    @Override
    public int getLoadingBarId() {
        return R.id.timetable_progress_bar;
    }

    @Override
    public List<TimetableHeaderItem> getItems() {
        List<Day> dayEntityList = getDaoSession().getAccountDao().load(getUserId()).getDayList();

        List<TimetableHeaderItem> dayList = new ArrayList<>();

        for (Day day : dayEntityList) {
            List<TimetableSubItem> timetableSubItems = new ArrayList<>();

            TimetableHeaderItem headerItem = new TimetableHeaderItem(day);

            for (Lesson lesson : day.getLessons()) {
                TimetableSubItem subItem = new TimetableSubItem(headerItem, lesson, getActivity());
                timetableSubItems.add(subItem);
            }

            headerItem.setExpanded(false);
            headerItem.setSubItems(timetableSubItems);
            dayList.add(headerItem);
        }
        return dayList;
    }

    @Override
    public void onRefresh() throws Exception {
        VulcanSynchronization synchronization = new VulcanSynchronization(new LoginSession());
        synchronization.loginCurrentUser(getContext(), getDaoSession(), new Vulcan());
        synchronization.syncTimetable();
    }

    @Override
    public void onPostRefresh(Boolean result, Activity activity) {
        if (result) {
            Snackbar.make(activity.findViewById(R.id.fragment_container),
                    R.string.timetable_refresh_success, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.refresh_error_text, Toast.LENGTH_SHORT).show();
        }
    }
}