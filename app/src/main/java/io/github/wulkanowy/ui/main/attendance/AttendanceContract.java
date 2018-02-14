package io.github.wulkanowy.ui.main.attendance;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface AttendanceContract {

    interface View extends BaseContract.View {
    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {
    }
}
