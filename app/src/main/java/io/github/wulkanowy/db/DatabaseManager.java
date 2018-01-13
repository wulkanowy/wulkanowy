package io.github.wulkanowy.db;


import org.greenrobot.greendao.query.QueryBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.db.dao.DaoHelper;
import io.github.wulkanowy.db.dao.entities.AccountDao;
import io.github.wulkanowy.db.dao.entities.DayDao;
import io.github.wulkanowy.db.dao.entities.GradeDao;
import io.github.wulkanowy.db.dao.entities.LessonDao;
import io.github.wulkanowy.db.dao.entities.SubjectDao;
import io.github.wulkanowy.db.dao.entities.WeekDao;
import io.github.wulkanowy.db.shared.SharedHelper;

@Singleton
public class DatabaseManager {

    public static final String DATABASE_NAME = "wulkanowy_db";

    public static final String SHARED_PREFERENCES_NAME = "user_data";

    private final DaoHelper daoHelper;

    private final SharedHelper sharedHelper;

    @Inject
    public DatabaseManager(DaoHelper daoHelper, SharedHelper sharedHelper) {
        this.daoHelper = daoHelper;
        this.sharedHelper = sharedHelper;
    }

    public long getCurrentUserId() {
        return sharedHelper.getCurrentUserId();
    }

    public void setCurrentUserId(long userId) {
        sharedHelper.setCurrentUserId(userId);
    }

    public AccountDao getAccountDao() {
        return daoHelper.getAccountDao();
    }

    public SubjectDao getSubjectDao() {
        return daoHelper.getSubjectDao();
    }

    public GradeDao getGradeDao() {
        return daoHelper.getGradeDao();
    }

    public WeekDao getWeekDao() {
        return daoHelper.getWeekDao();
    }

    public DayDao getDayDao() {
        return daoHelper.getDayDao();
    }

    public LessonDao getLessonDao() {
        return daoHelper.getLessonDao();
    }

    public QueryBuilder getGradeQuery() {
        return daoHelper.getGradeQuery();
    }
}
