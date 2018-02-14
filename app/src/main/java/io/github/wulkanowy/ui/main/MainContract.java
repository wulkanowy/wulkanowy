package io.github.wulkanowy.ui.main;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface MainContract {

    interface View extends BaseContract.View {

    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

    }
}
