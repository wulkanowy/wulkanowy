package io.github.wulkanowy.ui.main.grades;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.db.dao.entities.Lesson;
import io.github.wulkanowy.ui.base.BasePresenter;

public class GradesPresenter extends BasePresenter<GradesContract.View>
        implements GradesContract.Presenter {

    private boolean isFirstSight = false;

    @Inject
    public GradesPresenter(DatabaseManager databaseManager) {
        super(databaseManager);
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

            headerItems.add(new GradeHeaderItem(new Lesson().setSubject("Matemtyka")));

            getView().updateAdapterList(headerItems);
            getView().showProgressBar(false);
        }
    }
}
