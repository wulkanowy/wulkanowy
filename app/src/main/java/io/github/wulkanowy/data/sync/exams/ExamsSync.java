package io.github.wulkanowy.data.sync.exams;

import javax.inject.Inject;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;

public class ExamsSync implements ExamsSyncContract {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    @Inject
    public ExamsSync(DaoSession daoSession, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.vulcan = vulcan;
    }

    @Override
    public void sync(long diaryId) {

    }

    @Override
    public void sync(long diaryId, String date) {

    }
}
