package io.github.wulkanowy.services.synchronisation;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.greendao.query.Query;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import io.github.wulkanowy.utilities.DateHelper;

public class TimetableSynchronization {

    public void sync(@NonNull LoginSession loginSession, @Nullable Date dateOfMonday) throws NotLoggedInErrorException,
            IOException, ParseException {
        DayDao dayDao = loginSession.getDaoSession().getDayDao();
        LessonDao lessonDao = loginSession.getDaoSession().getLessonDao();

        Week week = dateOfMonday == null ? loginSession.getVulcan().getTimetable().getWeekTable()
                : loginSession.getVulcan().getTimetable()
                .getWeekTable(String.valueOf(DateHelper.getTicks(dateOfMonday)));


        List<Day> dayList = week.getDays();

        DayDao.dropTable(dayDao.getDatabase(), true);
        DayDao.createTable(dayDao.getDatabase(), false);

        dayDao.insertInTx(getPreparedDaysList(dayList, loginSession.getUserId()));

        Log.d(VulcanJobHelper.DEBUG_TAG, "Synchronization days (amount = " + dayList.size() + ")");


        LessonDao.dropTable(lessonDao.getDatabase(), true);
        LessonDao.createTable(lessonDao.getDatabase(), false);

        List<Lesson> lessonList = new ArrayList<>();
        lessonList.addAll(getPreparedLessonsList(dayList, dayDao));

        lessonDao.insertInTx(lessonList);

        Log.d(VulcanJobHelper.DEBUG_TAG, "Synchronization lessons (amount = " + lessonList.size() + ")");
    }

    private List<Lesson> getPreparedLessonsList(List<Day> dayList, DayDao dayDao) {
        List<Lesson> allLessonsList = new ArrayList<>();

        for (Day day : dayList) {

            Query<io.github.wulkanowy.dao.entities.Day> dayQuery = dayDao.queryBuilder()
                    .where(DayDao.Properties.Date.eq(day.getDate()))
                    .build();

            List<Lesson> lessonEntityList = ConversionVulcanObject.lessonsToLessonsEntities(day.getLessons());
            List<Lesson> updatedLessonEntityList = new ArrayList<>();

            for (Lesson lesson : lessonEntityList) {
                lesson.setDayId(dayQuery.uniqueOrThrow().getId());
                if (!"".equals(lesson.getSubject())) {
                    updatedLessonEntityList.add(lesson);
                }
            }
            allLessonsList.addAll(updatedLessonEntityList);
        }
        return allLessonsList;
    }

    private List<io.github.wulkanowy.dao.entities.Day> getPreparedDaysList(List<Day> dayList, long userId) {
        List<io.github.wulkanowy.dao.entities.Day> updatedDayList = new ArrayList<>();
        List<io.github.wulkanowy.dao.entities.Day> dayEntityList = ConversionVulcanObject
                .daysToDaysEntities(dayList);
        for (io.github.wulkanowy.dao.entities.Day day : dayEntityList) {
            day.setUserId(userId);
            updatedDayList.add(day);
        }
        return updatedDayList;
    }

    private Date getDateOfNextMonday() {
        Calendar calendar = Calendar.getInstance();

        int numberOfDaysAdd;
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            numberOfDaysAdd = 14;
        } else {
            numberOfDaysAdd = 7;
        }

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.DATE, numberOfDaysAdd);
        return calendar.getTime();
    }
}
