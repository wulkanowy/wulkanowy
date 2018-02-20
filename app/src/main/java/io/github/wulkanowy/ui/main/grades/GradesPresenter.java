package io.github.wulkanowy.ui.main.grades;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.LogUtils;

public class GradesPresenter extends BasePresenter<GradesContract.View>
        implements GradesContract.Presenter {

    private RefreshTask refreshTask;

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
            setItemsFromDb();
        }
    }

    @Override
    public void onRefresh() {
        if (getView().isNetworkConnected()) {
            refreshTask = new RefreshTask(this);
            refreshTask.execute();
        } else {
            getView().onNoNetworkError();
        }
    }

    @Override
    public void onCanceledAsync() {
        getView().hideRefreshingBar();
    }

    @Override
    public void onEndAsync(boolean success, Exception exception) {
        if (success) {
            setItemsFromDb();

            int numberOfNewGrades = getRepository().getNumberOfNewGrades();

            if (numberOfNewGrades <= 0) {
                getView().onRefreshSuccessNoGrade();
            } else {
                getView().onRefreshSuccess(numberOfNewGrades);
            }
        } else {
            LogUtils.error("An error occurred during the update of the data", exception);
            getView().onError(getRepository().getErrorLoginMessage(exception));
        }
        getView().hideRefreshingBar();
    }

    private void setItemsFromDb() {
        List<GradeHeaderItem> headerItems = new ArrayList<>();

        List<Subject> subjectList = getRepository().getCurrentUser().getSubjectList();

        for (Subject subject : subjectList) {
            List<Grade> gradeList = subject.getGradeList();

            if (!gradeList.isEmpty()) {
                GradeHeaderItem headerItem = new GradeHeaderItem(subject);

                List<GradesSubItem> subItems = new ArrayList<>();

                for (Grade grade : gradeList) {
                    subItems.add(new GradesSubItem(headerItem, grade));
                }
                headerItem.setSubItems(subItems);
                headerItem.setExpanded(false);
                headerItems.add(headerItem);
            }
        }

        if (headerItems.isEmpty()) {
            getView().showNoItem(true);
        } else {
            getView().updateAdapterList(headerItems);
            getView().showNoItem(false);
        }
        getView().showProgressBar(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
    }
}
