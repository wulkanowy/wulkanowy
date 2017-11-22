package io.github.wulkanowy.services.synchronisation;


import android.util.Log;

import org.greenrobot.greendao.query.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.timetable.Day;
import io.github.wulkanowy.api.timetable.Week;
import io.github.wulkanowy.dao.entities.DayDao;
import io.github.wulkanowy.dao.entities.Lesson;
import io.github.wulkanowy.dao.entities.LessonDao;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.utilities.ConversionVulcanObject;

public class TimetableSynchronization {

    public void sync(LoginSession loginSession) throws NotLoggedInErrorException, IOException {

        DayDao dayDao = loginSession.getDaoSession().getDayDao();
        LessonDao lessonDao = loginSession.getDaoSession().getLessonDao();

        Week week = loginSession.getVulcan().getTimetable().getWeekTable();

        List<Day> dayList = week.getDays();
        List<io.github.wulkanowy.dao.entities.Day> dayEntityList = ConversionVulcanObject
                .daysToDaysEntities(dayList);
        List<io.github.wulkanowy.dao.entities.Day> updatedDayEntityList = new ArrayList<>();

        DayDao.dropTable(dayDao.getDatabase(), true);
        DayDao.createTable(dayDao.getDatabase(), false);

        for (io.github.wulkanowy.dao.entities.Day day : dayEntityList) {
            day.setUserId(loginSession.getUserId());
            updatedDayEntityList.add(day);
        }

        dayDao.insertInTx(updatedDayEntityList);


        LessonDao.dropTable(lessonDao.getDatabase(), true);
        LessonDao.createTable(lessonDao.getDatabase(), false);

        int amount = 0;

        for (Day day : dayList) {

            Query<io.github.wulkanowy.dao.entities.Day> dayQuery = dayDao.queryBuilder()
                    .where(DayDao.Properties.Date.eq(day.getDate()))
                    .build();

            List<Lesson> lessonEntityList = ConversionVulcanObject.lessonsToLessonsEntities(day.getLessons());
            List<Lesson> updatedLessonEntityList = new ArrayList<>();

            for (Lesson lesson : lessonEntityList) {
                lesson.setDayId(dayQuery.uniqueOrThrow().getId());
                updatedLessonEntityList.add(lesson);
            }

            lessonDao.insertInTx(updatedLessonEntityList);
            amount += updatedLessonEntityList.size();
        }

        Log.d(VulcanJobHelper.DEBUG_TAG, "Synchronization lessons (amount = " + String.valueOf(amount + ")"));
    }
}
