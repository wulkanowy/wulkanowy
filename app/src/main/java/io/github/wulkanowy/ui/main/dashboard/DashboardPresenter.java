package io.github.wulkanowy.ui.main.dashboard;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.ui.base.BasePresenter;

public class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    @Inject
    public DashboardPresenter(DatabaseManager databaseManager) {
        super(databaseManager);
    }
}
