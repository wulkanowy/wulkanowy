package io.github.wulkanowy.ui.main.dashboard;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface DashboardContract {

    interface View extends BaseContract.View {
    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {
    }
}
