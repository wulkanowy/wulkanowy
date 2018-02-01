package io.github.wulkanowy.ui.main.attendance;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.ui.base.BasePresenter;

public class AttendancePresenter extends BasePresenter<AttendanceContract.View>
        implements AttendanceContract.Presenter {

    @Inject
    public AttendancePresenter(DatabaseManager databaseManager) {
        super(databaseManager);
    }
}
