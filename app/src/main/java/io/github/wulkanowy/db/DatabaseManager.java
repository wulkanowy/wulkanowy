package io.github.wulkanowy.db;


import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.db.dao.AppDatabase;
import io.github.wulkanowy.db.dao.entities.AccountDao;
import io.github.wulkanowy.db.dao.entities.DayDao;
import io.github.wulkanowy.db.dao.entities.GradeDao;
import io.github.wulkanowy.db.dao.entities.LessonDao;
import io.github.wulkanowy.db.dao.entities.SubjectDao;
import io.github.wulkanowy.db.dao.entities.WeekDao;
import io.github.wulkanowy.db.resources.AppResources;
import io.github.wulkanowy.db.shared.AppShared;

@Singleton
public class DatabaseManager implements AppDatabase, AppResources.ResourcesManager, AppShared {

    private final AppDatabase appDatabase;

    private final AppShared appShared;

    private final AppResources appResources;

    @Inject
    public DatabaseManager(AppDatabase appDatabase, AppShared appShared,
                           AppResources appResources) {
        this.appDatabase = appDatabase;
        this.appShared = appShared;
        this.appResources = appResources;
    }

    @Override
    public long getCurrentUserId() {
        return appShared.getCurrentUserId();
    }

    @Override
    public void setCurrentUserId(long userId) {
        appShared.setCurrentUserId(userId);
    }

    @Override
    public AccountDao getAccountDao() {
        return appDatabase.getAccountDao();
    }

    @Override
    public SubjectDao getSubjectDao() {
        return appDatabase.getSubjectDao();
    }

    @Override
    public GradeDao getGradeDao() {
        return appDatabase.getGradeDao();
    }

    @Override
    public WeekDao getWeekDao() {
        return appDatabase.getWeekDao();
    }

    @Override
    public DayDao getDayDao() {
        return appDatabase.getDayDao();
    }

    @Override
    public LessonDao getLessonDao() {
        return appDatabase.getLessonDao();
    }

    @Override
    public AppResources getAppResources() {
        return appResources;
    }
}
