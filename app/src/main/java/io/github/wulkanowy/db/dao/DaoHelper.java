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
public class DaoHelper implements DaoAccess {

    private final DaoSession daoSession;

    @Inject
    public DaoHelper(DbHelper dbHelper) {
        daoSession = new DaoMaster(dbHelper.getWritableDb()).newSession();
    }

    @Override
    public AccountDao getAccountDao() {
        return daoSession.getAccountDao();
    }

    @Override
    public SubjectDao getSubjectDao() {
        return daoSession.getSubjectDao();
    }

    @Override
    public GradeDao getGradeDao() {
        return daoSession.getGradeDao();
    }

    @Override
    public WeekDao getWeekDao() {
        return daoSession.getWeekDao();
    }

    @Override
    public DayDao getDayDao() {
        return daoSession.getDayDao();
    }

    @Override
    public LessonDao getLessonDao() {
        return daoSession.getLessonDao();
    }

    @Override
    public QueryBuilder getGradeQuery() {
        return getGradeDao().queryBuilder();
    }
}
