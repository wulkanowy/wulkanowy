package io.github.wulkanowy.services.synchronisation;


import android.util.Log;

import org.greenrobot.greendao.query.Query;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    public void sync(LoginSession loginSession) throws NotLoggedInErrorException, IOException, ParseException {
        DayDao dayDao = loginSession.getDaoSession().getDayDao();
        LessonDao lessonDao = loginSession.getDaoSession().getLessonDao();

        Week currentWeek = loginSession.getVulcan().getTimetable().getWeekTable();
        Week nextWeek = loginSession.getVulcan().getTimetable()
                .getWeekTable(String.valueOf(DateHelper.getTicks(getDateOfNextMonday())));

        List<Day> currentDayList = currentWeek.getDays();
        List<Day> nextDayList = nextWeek.getDays();

        DayDao.dropTable(dayDao.getDatabase(), true);
        DayDao.createTable(dayDao.getDatabase(), false);

        List<io.github.wulkanowy.dao.entities.Day> allDayList = new ArrayList<>();
        allDayList.addAll(getPreparedDaysList(currentDayList, loginSession.getUserId()));
        allDayList.addAll(getPreparedDaysList(nextDayList, loginSession.getUserId()));

        dayDao.insertInTx(allDayList);

        Log.d(VulcanJobHelper.DEBUG_TAG, "Synchronization days (amount = " + allDayList.size() + ")");

        LessonDao.dropTable(lessonDao.getDatabase(), true);
        LessonDao.createTable(lessonDao.getDatabase(), false);

        List<Lesson> allLessonList = new ArrayList<>();
        allLessonList.addAll(getPreparedLessonsList(currentDayList, dayDao));
        allLessonList.addAll(getPreparedLessonsList(nextDayList, dayDao));

        lessonDao.insertInTx(allLessonList);

        Log.d(VulcanJobHelper.DEBUG_TAG, "Synchronization lessons (amount = " + allLessonList.size() + ")");
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

    private List<io.github.wulkanowy.dao.entities.Day> getPreparedDaysList(List<Day> dayList, long userId){
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
        Log.e("DUPA", calendar.getTime().toString());
        return calendar.getTime();
    }
}
