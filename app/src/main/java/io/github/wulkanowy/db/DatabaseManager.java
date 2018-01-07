package io.github.wulkanowy.db;


import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.db.dao.DaoAccess;
import io.github.wulkanowy.db.dao.entities.AccountDao;
import io.github.wulkanowy.db.dao.entities.DayDao;
import io.github.wulkanowy.db.dao.entities.GradeDao;
import io.github.wulkanowy.db.dao.entities.LessonDao;
import io.github.wulkanowy.db.dao.entities.SubjectDao;
import io.github.wulkanowy.db.dao.entities.WeekDao;
import io.github.wulkanowy.db.shared.SharedAccess;
import io.github.wulkanowy.di.annotations.ApplicationContext;

@Singleton
public class DatabaseManager implements SharedAccess, DaoAccess {

    public static final String DATABASE_NAME = "wulkanowy_db";

    public static final String SHARED_PREFERNCES_NAME = "user_data";

    private final Context context;

    private final DaoAccess daoAccess;

    private final SharedAccess sharedAccess;

    @Inject
    public DatabaseManager(@ApplicationContext Context context,
                           DaoAccess daoAccess,
                           SharedAccess sharedAccess) {
        this.context = context;
        this.daoAccess = daoAccess;
        this.sharedAccess = sharedAccess;
    }

    @Override
    public long getCurrentUserId() {
        return sharedAccess.getCurrentUserId();
    }

    @Override
    public void setCurrentUserId(long userId) {
        sharedAccess.setCurrentUserId(userId);
    }

    @Override
    public AccountDao getAccountDao() {
        return daoAccess.getAccountDao();
    }

    @Override
    public SubjectDao getSubjectDao() {
        return daoAccess.getSubjectDao();
    }

    @Override
    public GradeDao getGradeDao() {
        return daoAccess.getGradeDao();
    }

    @Override
    public WeekDao getWeekDao() {
        return daoAccess.getWeekDao();
    }

    @Override
    public DayDao getDayDao() {
        return daoAccess.getDayDao();
    }

    @Override
    public LessonDao getLessonDao() {
        return daoAccess.getLessonDao();
    }

    @Override
    public QueryBuilder getGradeQuery() {
        return daoAccess.getGradeQuery();
    }
}
