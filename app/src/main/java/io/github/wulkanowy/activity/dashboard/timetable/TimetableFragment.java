package io.github.wulkanowy.activity.dashboard.timetable;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.AbstractFragment;
import io.github.wulkanowy.activity.dashboard.AbstractRefreshTask;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Day;
import io.github.wulkanowy.dao.entities.Lesson;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;

public class TimetableFragment extends AbstractFragment<TimetableHeaderItem> {

    @Override
    public int getLayout() {
        return R.layout.fragment_timetable;
    }

    @Override
    public int getRecyclerView() {
        return R.id.timetable_recycler;
    }

    @Override
    public int getRefreshLayout() {
        return R.id.timetable_refresh_layout;
    }

    @Override
    public int getLoadingBar() {
        return R.id.timetable_progress_bar;
    }

    @Override
    public List<TimetableHeaderItem> getItems(DaoSession daoSession) {
        List<Day> dayEntityList = daoSession.getAccountDao().load(getUserId()).getDayList();

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


    private static class RefreshTask extends AbstractRefreshTask {

        public RefreshTask(View mainView, DaoSession daoSession) {
            super(mainView, daoSession);
        }

        @Override
        protected void executeInBackground() throws Exception {
            VulcanSynchronization synchronization = new VulcanSynchronization(new LoginSession());
            synchronization.loginCurrentUser(getContext(), getDaoSession(), new Vulcan());
            synchronization.syncTimetable();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Snackbar.make(getMainView().getRootView(), R.string.timetable_refresh_success,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.refresh_error_text, Toast.LENGTH_SHORT).show();
            }

            SwipeRefreshLayout swipeRefreshLayout = getMainView().findViewById(R.id.timetable_refresh_layout);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
