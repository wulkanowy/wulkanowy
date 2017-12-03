package io.github.wulkanowy.activity.dashboard.timetable;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
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
            headerItem.setExpanded(false);

            for (Lesson lesson : day.getLessons()) {
                TimetableSubItem subItem = new TimetableSubItem(headerItem, lesson, getActivity());
                timetableSubItems.add(subItem);
            }

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
                Date date = dateFormat.parse(day.getDate());

                Calendar calendar = Calendar.getInstance();

                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    calendar.add(Calendar.DATE, 1);
                } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    calendar.add(Calendar.DATE, 2);
                }

                calendar = zeroingCalendar(calendar);

                if (date.compareTo(calendar.getTime()) == 0) {
                    headerItem.setExpanded(true);
                }

            } catch (Exception e) {
                Log.e(WulkanowyApp.DEBUG_TAG, "Parse failed", e);
            }

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
    public void onPostRefresh(Boolean result) {
        if (result) {
            Snackbar.make(getActivityWeakReference().findViewById(R.id.fragment_container),
                    R.string.timetable_refresh_success, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.refresh_error_text, Toast.LENGTH_SHORT).show();
        }
    }

    private Calendar zeroingCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}