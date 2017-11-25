package io.github.wulkanowy.activity.dashboard.timetable;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Day;
import io.github.wulkanowy.dao.entities.Lesson;

public class TimetableFragment extends Fragment {

    private static List<TimetableHeaderItem> dayList = new ArrayList<>();

    private static long userId;

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
                createExpList(view, this);
                new GenerateListTask(this, view, daoSession).execute();
            } else {
                createExpList(view, this);
                view.findViewById(R.id.timetable_progress_bar).setVisibility(View.INVISIBLE);
            }
        }


        return view;
    }

    public static void createExpList(View mainView, Fragment fragment) {
        FlexibleAdapter<TimetableHeaderItem> flexibleAdapter = new FlexibleAdapter<>(dayList, fragment);
        flexibleAdapter.setDisplayHeadersAtStartUp(true);
        flexibleAdapter.setAutoCollapseOnExpand(false);
        flexibleAdapter.setAutoScrollOnExpand(false);
        flexibleAdapter.expandItemsAtStartUp();

        RecyclerView recyclerView = mainView.findViewById(R.id.timetable_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        recyclerView.setAdapter(flexibleAdapter);
    }

    public static void downloadTimetableFromDatabase(DaoSession daoSession) {

        List<Day> dayEntityList = daoSession.getAccountDao().load(userId).getDayList();

        dayList = new ArrayList<>();

        for (Day day : dayEntityList) {
            List<TimetableSubItem> timetableSubItems = new ArrayList<>();

            TimetableHeaderItem headerItem = new TimetableHeaderItem(day);

            for (Lesson lesson : day.getLessons()) {
                TimetableSubItem subItem = new TimetableSubItem(headerItem, lesson);
                timetableSubItems.add(subItem);
            }

            headerItem.setExpanded(false);
            headerItem.setSubItems(timetableSubItems);
            dayList.add(headerItem);
        }
    }

    private static class GenerateListTask extends AsyncTask<Void, Void, Void> {

        private DaoSession daoSession;

        private WeakReference<Fragment> fragment;

        private WeakReference<View> mainView;

        public GenerateListTask(Fragment actualFragment, View mainView, DaoSession daoSession) {
            this.daoSession = daoSession;
            this.fragment = new WeakReference<>(actualFragment);
            this.mainView = new WeakReference<>(mainView);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            downloadTimetableFromDatabase(daoSession);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            createExpList(mainView.get(), fragment.get());
            mainView.get().findViewById(R.id.timetable_progress_bar).setVisibility(View.INVISIBLE);
        }
    }
}
