package io.github.wulkanowy.db.dao;

import org.greenrobot.greendao.query.QueryBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.db.dao.entities.AccountDao;
import io.github.wulkanowy.db.dao.entities.DaoMaster;
import io.github.wulkanowy.db.dao.entities.DaoSession;
import io.github.wulkanowy.db.dao.entities.DayDao;
import io.github.wulkanowy.db.dao.entities.GradeDao;
import io.github.wulkanowy.db.dao.entities.LessonDao;
import io.github.wulkanowy.db.dao.entities.SubjectDao;
import io.github.wulkanowy.db.dao.entities.WeekDao;

@Singleton
public class DaoHelper {

    private final DaoSession daoSession;

    @Inject
    public DaoHelper(DbHelper dbHelper) {
        daoSession = new DaoMaster(dbHelper.getWritableDb()).newSession();
    }

    public AccountDao getAccountDao() {
        return daoSession.getAccountDao();
    }

    public SubjectDao getSubjectDao() {
        return daoSession.getSubjectDao();
    }

    public GradeDao getGradeDao() {
        return daoSession.getGradeDao();
    }

    public WeekDao getWeekDao() {
        return daoSession.getWeekDao();
    }

    public DayDao getDayDao() {
        return daoSession.getDayDao();
    }

    public LessonDao getLessonDao() {
        return daoSession.getLessonDao();
    }

    public QueryBuilder getGradeQuery() {
        return getGradeDao().queryBuilder();
    }
}
