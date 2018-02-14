package io.github.wulkanowy.ui.main.dashboard;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;

public class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    @Inject
    DashboardPresenter(RepositoryContract repository) {
        super(repository);
    }
}
