package io.github.wulkanowy.ui.main.grades;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.ui.base.BasePresenter;

public class GradesPresenter extends BasePresenter<GradesContract.View>
        implements GradesContract.Presenter {

    private boolean isFirstSight = false;

    @Inject
    GradesPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(@NonNull GradesContract.View view) {
        super.onStart(view);

        getView().showProgressBar(true);
    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        if (isVisible && !isFirstSight) {
            isFirstSight = true;

            List<GradeHeaderItem> headerItems = new ArrayList<>();

            List<Subject> subjectList = getRepository().getCurrentUser().getSubjectList();

            for (Subject subject : subjectList) {
                List<Grade> gradeList = subject.getGradeList();

                if (!gradeList.isEmpty()) {
                    GradeHeaderItem headerItem = new GradeHeaderItem(subject);

                    for (Grade grade : gradeList) {
                        headerItem.addSubItem(new GradesSubItem(headerItem, grade));
                        headerItem.setExpanded(false);

                        if (!grade.getRead()) {
                            headerItem.setAlertSubItemVisible();
                        }
                    }
                    headerItems.add(headerItem);
                }
            }

            if (headerItems.isEmpty()) {
                getView().setVisibleNoItem();
            } else {
                getView().updateAdapterList(headerItems);
            }
            getView().showProgressBar(false);
        }
    }

    @Override
    public void onRefresh() {
        if (getView().isNetworkConnected()) {
            getView().onError("SUCCES");
        } else {
            getView().onNoNetworkError();
        }
        getView().hideRefreshingBar();
    }
}
