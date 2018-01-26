package io.github.wulkanowy.db.dao;

import io.github.wulkanowy.db.dao.entities.AccountDao;
import io.github.wulkanowy.db.dao.entities.DayDao;
import io.github.wulkanowy.db.dao.entities.GradeDao;
import io.github.wulkanowy.db.dao.entities.LessonDao;
import io.github.wulkanowy.db.dao.entities.SubjectDao;
import io.github.wulkanowy.db.dao.entities.WeekDao;

public interface AppDatabase {

    AccountDao getAccountDao();

    SubjectDao getSubjectDao();

    GradeDao getGradeDao();

    WeekDao getWeekDao();

    DayDao getDayDao();

    LessonDao getLessonDao();
}
