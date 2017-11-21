package io.github.wulkanowy.services.synchronisation;


import android.util.Log;

import java.io.IOException;
import java.util.List;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.timetable.Day;
import io.github.wulkanowy.api.timetable.Lesson;
import io.github.wulkanowy.api.timetable.Week;
import io.github.wulkanowy.dao.entities.DayDao;
import io.github.wulkanowy.dao.entities.LessonDao;
import io.github.wulkanowy.dao.entities.WeekDao;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.utilities.ConversionVulcanObject;

public class TimetableSynchronization {

    public void sync(LoginSession loginSession) throws NotLoggedInErrorException, IOException {

        WeekDao weekDao = loginSession.getDaoSession().getWeekDao();
        DayDao dayDao = loginSession.getDaoSession().getDayDao();
        LessonDao lessonDao = loginSession.getDaoSession().getLessonDao();

        Week week = loginSession.getVulcan().getTimetable().getWeekTable();
        WeekDao.dropTable(weekDao.getDatabase(), true);
        WeekDao.createTable(weekDao.getDatabase(), false);
        weekDao.insert(ConversionVulcanObject.weeksToWeekEntities(week));

        List<Day> dayList = week.getDays();
        DayDao.dropTable(dayDao.getDatabase(), true);
        DayDao.createTable(dayDao.getDatabase(), false);
        dayDao.insertInTx(ConversionVulcanObject.daysToDaysEntities(dayList));

        LessonDao.dropTable(lessonDao.getDatabase(), true);
        LessonDao.createTable(lessonDao.getDatabase(), false);

        int amount = 0;

        for (Day day : dayList) {
            List<Lesson> lessonList = day.getLessons();
            lessonDao.insertInTx(ConversionVulcanObject.lessonsToLessonsEntities(lessonList));
            amount += lessonList.size();
        }

        Log.d(VulcanJobHelper.DEBUG_TAG, "Synchronization lessons (amount = " + String.valueOf(amount + ")"));
    }
}
