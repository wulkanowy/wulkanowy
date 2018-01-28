package io.github.wulkanowy.ui.main.grades;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.ui.base.BasePresenter;

public class GradesPresenter extends BasePresenter<GradesContract.View>
        implements GradesContract.Presenter {

    @Inject
    public GradesPresenter(DatabaseManager databaseManager) {
        super(databaseManager);
    }
}
