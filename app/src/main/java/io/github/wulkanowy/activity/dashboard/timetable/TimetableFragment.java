package io.github.wulkanowy.activity.dashboard.timetable;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Day;
import io.github.wulkanowy.dao.entities.Lesson;

public class TimetableFragment extends Fragment {

    private List<TimetableHeaderItem> dayList = new ArrayList<>();

    private long userId;

    public TimetableFragment() {
        //empty constructor for fragments
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        if (getActivity() != null) {
            DaoSession daoSession = ((WulkanowyApp) getActivity().getApplication()).getDaoSession();
            userId = getActivity().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    .getLong("userId", 0);

            if (dayList.isEmpty()) {
                downloadTimetableFromDatabase(daoSession);
                createViewWithAdapter(view);
                setLoadingBarInvisible(view);
            } else {
                createViewWithAdapter(view);
                setLoadingBarInvisible(view);
            }
        }


        return view;
    }

    private void setLoadingBarInvisible(View mainView) {
        mainView.findViewById(R.id.timetable_progress_bar).setVisibility(View.INVISIBLE);
    }

    private void createViewWithAdapter(View mainView) {
        FlexibleAdapter<TimetableHeaderItem> flexibleAdapter = new FlexibleAdapter<>(dayList);
        flexibleAdapter.setDisplayHeadersAtStartUp(true);
        flexibleAdapter.setAutoCollapseOnExpand(false);
        flexibleAdapter.setAutoScrollOnExpand(true);
        flexibleAdapter.expandItemsAtStartUp();

        RecyclerView recyclerView = mainView.findViewById(R.id.timetable_recycler);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(mainView.getContext()));
        recyclerView.setAdapter(flexibleAdapter);
    }

    private void downloadTimetableFromDatabase(DaoSession daoSession) {

        List<Day> dayEntityList = daoSession.getAccountDao().load(userId).getDayList();

        dayList = new ArrayList<>();

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
    }
}
