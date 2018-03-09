package io.github.wulkanowy.data.sync.timetable;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.generic.Lesson;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.DayDao;
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson;
import io.github.wulkanowy.data.db.dao.entities.TimetableLessonDao;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.dao.entities.WeekDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.LogUtils;
import io.github.wulkanowy.utils.TimeUtils;

@Singleton
public class TimetableSync implements TimetableSyncContract {

    private final DaoSession daoSession;

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    private long userId;

    @Inject
    TimetableSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void syncTimetable() throws NotLoggedInErrorException, IOException, ParseException {
        syncTimetable(null);
    }

    @Override
    public void syncTimetable(String date) throws NotLoggedInErrorException, IOException, ParseException {
        this.userId = sharedPref.getCurrentUserId();

        io.github.wulkanowy.api.generic.Week<io.github.wulkanowy.api.generic.Day> weekApi = getWeekFromApi(getNormalizedDate(date));
        Week weekDb = getWeekFromDb(weekApi.getStartDayDate());

        long weekId = updateWeekInDb(weekDb, weekApi);

        List<TimetableLesson> lessonList = updateDays(weekApi.getDays(), weekId);

        daoSession.getTimetableLessonDao().saveInTx(lessonList);

        LogUtils.debug("Synchronization lessons (amount = " + lessonList.size() + ")");
    }

    private String getNormalizedDate(String date) throws ParseException {
        return null != date ? String.valueOf(TimeUtils.getNetTicks(date)) : "";
    }

    private io.github.wulkanowy.api.generic.Week<io.github.wulkanowy.api.generic.Day> getWeekFromApi(String date)
            throws IOException, NotLoggedInErrorException, ParseException {
        return vulcan.getTimetable().getWeekTable(date);
    }

    private Week getWeekFromDb(String date) {
        return daoSession.getWeekDao()
                .queryBuilder()
                .where(WeekDao.Properties.UserId.eq(userId), WeekDao.Properties.StartDayDate.eq(date))
                .unique();
    }

    private Long updateWeekInDb(Week fromDb, io.github.wulkanowy.api.generic.Week fromApi) {
        if (fromDb != null) {
            fromDb.setIsTimetableSynced(true);
            fromDb.update();

            return fromDb.getId();
        }

        Week weekFromNetEntity = DataObjectConverter.weekToWeekEntity(fromApi).setUserId(userId);
        weekFromNetEntity.setIsTimetableSynced(true);

        return daoSession.getWeekDao().insert(weekFromNetEntity);
    }

    private List<TimetableLesson> updateDays(List<io.github.wulkanowy.api.generic.Day> dayListFromApi, long weekId) {
        List<TimetableLesson> updatedLessonList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Day dayFromApi : dayListFromApi) {

            Day dayFromDb = getDayFromDb(dayFromApi.getDate());

            Day dayFromApiEntity = DataObjectConverter.dayToDayEntity(dayFromApi);

            long dayId = updateDay(dayFromDb, dayFromApiEntity, weekId);

            updateLessons(dayFromApi.getLessons(), updatedLessonList, dayId);
        }

        return updatedLessonList;
    }

    private Day getDayFromDb(String date) {
        return daoSession.getDayDao()
                .queryBuilder()
                .where(DayDao.Properties.UserId.eq(userId), DayDao.Properties.Date.eq(date))
                .unique();
    }

    private long updateDay(Day dayFromDb, Day dayFromApiEntity, long weekId) {
        dayFromApiEntity.setUserId(userId);
        dayFromApiEntity.setWeekId(weekId);

        if (null != dayFromDb) {
            dayFromApiEntity.setId(dayFromDb.getId());

            daoSession.getDayDao().save(dayFromApiEntity);
            dayFromDb.refresh();

            return dayFromDb.getId();
        }

        return daoSession.getDayDao().insert(dayFromApiEntity);
    }

    private void updateLessons(List<Lesson> lessons, List<TimetableLesson> updatedLessons, long dayId) {
        List<TimetableLesson> lessonsFromApiEntities = DataObjectConverter
                .lessonsToTimetableLessonsEntities(lessons);

        for (TimetableLesson lessonFromApiEntity : lessonsFromApiEntities) {
            TimetableLesson lessonFromDb = getLessonFromDb(lessonFromApiEntity, dayId);

            lessonFromApiEntity.setDayId(dayId);

            if (lessonFromDb != null) {
                lessonFromApiEntity.setId(lessonFromDb.getId());
            }

            if (!"".equals(lessonFromApiEntity.getSubject())) {
                updatedLessons.add(lessonFromApiEntity);
            }
        }
    }

    private TimetableLesson getLessonFromDb(TimetableLesson lessonFromNetEntity, long dayId) {
        return daoSession.getTimetableLessonDao().queryBuilder()
                .where(TimetableLessonDao.Properties.DayId.eq(dayId),
                        TimetableLessonDao.Properties.Date.eq(lessonFromNetEntity.getDate()),
                        TimetableLessonDao.Properties.StartTime.eq(lessonFromNetEntity.getStartTime()),
                        TimetableLessonDao.Properties.EndTime.eq(lessonFromNetEntity.getEndTime()))
                .unique();
    }
}
