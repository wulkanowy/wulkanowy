package io.github.wulkanowy.activity.dashboard.grades;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.wulkanowy.R;
import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.Subject;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.database.accounts.Account;
import io.github.wulkanowy.database.accounts.AccountsDatabase;
import io.github.wulkanowy.database.grades.GradesDatabase;
import io.github.wulkanowy.database.subjects.SubjectsDatabase;

public class GradesFragment extends Fragment {

    private List<SubjectWithGrades> subjectWithGradesList = new ArrayList<>();

    private View view;

    ExpandableGroup expandableGroup = null;

    GradesAdapter gradesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_marks, container, false);

        if (subjectWithGradesList.size() == 0) {
            new MarksTask(container.getContext()).execute();
        } else if (subjectWithGradesList.size() > 1) {
            createGrid();
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        return view;
    }

    public void createGrid() {

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.subject_grade_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        gradesAdapter = new GradesAdapter(subjectWithGradesList);
        /*gradesAdapter.setOnGroupExpandCollapseListener(new GroupExpandCollapseListener() {
            @Override
            public void onGroupExpanded(ExpandableGroup group) {
                if (expandableGroup == null) {
                    expandableGroup = group;
                } else if (!expandableGroup.equals(group)) {
                    gradesAdapter.toggleGroup(expandableGroup);
                    expandableGroup = group;
                }
            }

            @Override
            public void onGroupCollapsed(ExpandableGroup group) {
                if (group.equals(expandableGroup)) {
                    expandableGroup = null;
                }

            }
        });*/
        recyclerView.setAdapter(gradesAdapter);

    }

    public class MarksTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private Map<String, String> loginCookies;

        MarksTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String cookiesPath = mContext.getFilesDir().getPath() + "/cookies.txt";
            long userId = mContext.getSharedPreferences("LoginData", mContext.MODE_PRIVATE).getLong("isLogin", 0);

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cookiesPath));
                loginCookies = (Map<String, String>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Cookies cookies = new Cookies();
                cookies.setItems(loginCookies);

                AccountsDatabase accountsDatabase = new AccountsDatabase(mContext);
                accountsDatabase.open();
                Account account = accountsDatabase.getAccount(userId);
                accountsDatabase.close();

                StudentAndParent snp = new StudentAndParent(cookies, account.getCounty());
                SubjectsList subjectsList = new SubjectsList(snp);

                SubjectsDatabase subjectsDatabase = new SubjectsDatabase(mContext);
                subjectsDatabase.open();
                subjectsDatabase.put(subjectsList.getAll());
                subjectsDatabase.close();


                GradesList gradesList = new GradesList(snp);
                GradesDatabase gradesDatabase = new GradesDatabase(mContext);
                gradesDatabase.open();
                gradesDatabase.put(gradesList.getAll());

                for (Subject subject : subjectsList.getAll()) {
                    List<GradeItem> gradeItems = gradesDatabase.getSubjectGrades(userId, SubjectsDatabase.getSubjectId(subject.getName()));
                    if (gradeItems.size() > 0) {
                        subjectWithGradesList.add(new SubjectWithGrades(subject.getName(), gradeItems));
                    }
                }

                gradesDatabase.close();


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            createGrid();

            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            super.onPostExecute(result);
        }
    }
}
