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
    public GradesPresenter(RepositoryContract repository) {
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
                GradeHeaderItem headerItem = new GradeHeaderItem(subject);

                for (Grade grade : subject.getGradeList()) {
                    headerItem.addSubItem(new GradesSubItem(headerItem, grade));
                    headerItem.setExpanded(false);
                }

                headerItems.add(headerItem);
            }

            getView().updateAdapterList(headerItems);
            getView().showProgressBar(false);
        }
    }
}
