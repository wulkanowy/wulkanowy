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
import io.github.wulkanowy.db.resources.ResourcesHelper;
import io.github.wulkanowy.db.shared.SharedHelper;

@Singleton
public class DatabaseManager {

    private final DaoHelper daoHelper;

    private final SharedHelper sharedHelper;

    private final ResourcesHelper resourcesHelper;

    @Inject
    public DatabaseManager(DaoHelper daoHelper, SharedHelper sharedHelper,
                           ResourcesHelper resourcesHelper) {
        this.daoHelper = daoHelper;
        this.sharedHelper = sharedHelper;
        this.resourcesHelper = resourcesHelper;
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

    public ResourcesHelper getAppResources() {
        return resourcesHelper;
    }
}
