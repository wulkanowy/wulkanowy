package io.github.wulkanowy.activity.dashboard.grades;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Grade;
import io.github.wulkanowy.dao.entities.Subject;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;
import io.github.wulkanowy.services.jobs.VulcanSync;
import io.github.wulkanowy.utilities.ConnectionUtilities;

public class GradesFragment extends Fragment {

    private List<SubjectWithGrades> subjectWithGradesList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    private View view;

    private Fragment thisFragment = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grades, container, false);

        swipeRefreshLayout = view.findViewById(R.id.grade_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!ConnectionUtilities.isOnline(view.getContext())) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(view.getContext(), R.string.noInternet_text, Toast.LENGTH_SHORT).show();
                } else {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            DaoSession daoSession = ((WulkanowyApp) getActivity().getApplication()).getDaoSession();
                            VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
                            try {
                                vulcanSynchronization.loginCurrentUser(getContext(), daoSession, new Vulcan());
                                vulcanSynchronization.syncGrades();
                                prepareSubjectsWithGradesList(daoSession);
                            } catch (Exception e) {
                                Log.e(VulcanSync.DEBUG_TAG, "There was a synchronization problem", e);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            getFragmentManager().beginTransaction().detach(thisFragment).attach(thisFragment).commit();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }.execute();
                }
            }
        });

        if (new ArrayList<>().equals(subjectWithGradesList)) {
            createExpListView();
            new GradesTask(((WulkanowyApp) getActivity().getApplication()).getDaoSession()).execute();
        } else if (subjectWithGradesList.size() > 0) {
            createExpListView();
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        return view;
    }

    private void createExpListView() {

        RecyclerView recyclerView = view.findViewById(R.id.subject_grade_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        GradesAdapter gradesAdapter = new GradesAdapter(subjectWithGradesList, view.getContext());
        recyclerView.setAdapter(gradesAdapter);

    }

    private void prepareSubjectsWithGradesList(DaoSession daoSession) {
        subjectWithGradesList = new ArrayList<>();

        long userId = getActivity().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                .getLong("userId", 0);

        AccountDao accountDao = daoSession.getAccountDao();
        Account account = accountDao.load(userId);

        for (Subject subject : account.getSubjectList()) {
            List<Grade> gradeList = subject.getGradeList();
            if (gradeList.size() != 0) {
                SubjectWithGrades subjectWithGrades = new SubjectWithGrades(subject.getName(), gradeList);
                subjectWithGradesList.add(subjectWithGrades);
            }
        }
    }

    private class GradesTask extends AsyncTask<Void, Void, Void> {

        private DaoSession daoSession;

        GradesTask(DaoSession daoSession) {
            this.daoSession = daoSession;
        }

        @Override
        protected Void doInBackground(Void... params) {
            prepareSubjectsWithGradesList(daoSession);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            createExpListView();

            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }
}
