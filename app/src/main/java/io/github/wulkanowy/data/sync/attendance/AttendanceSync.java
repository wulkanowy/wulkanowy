package io.github.wulkanowy.data.sync.attendance;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.generic.Lesson;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLessonDao;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.DayDao;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.dao.entities.WeekDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.LogUtils;
import io.github.wulkanowy.utils.TimeUtils;

@Singleton
public class AttendanceSync implements AttendanceSyncContract {

    private final DaoSession daoSession;

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    private long userId;

    @Inject
    AttendanceSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void syncAttendance(String date) throws IOException, NotLoggedInErrorException, ParseException {
        this.userId = sharedPref.getCurrentUserId();

        io.github.wulkanowy.api.generic.Week<io.github.wulkanowy.api.generic.Day> weekApi = getWeekFromApi(getNormalizedDate(date));
        Week weekDb = getWeekFromDb(weekApi.getStartDayDate());

        long weekId = updateWeekInDb(weekDb, weekApi);

        List<AttendanceLesson> lessonList = updateDays(weekApi.getDays(), weekId);

        daoSession.getAttendanceLessonDao().saveInTx(lessonList);

        LogUtils.debug("Synchronization lessons (amount = " + lessonList.size() + ")");
    }

    @Override
    public void syncAttendance() throws IOException, NotLoggedInErrorException, ParseException {
        syncAttendance(null);
    }

    private String getNormalizedDate(String date) throws ParseException {
        return null != date ? String.valueOf(TimeUtils.getNetTicks(date)) : "";
    }

    private io.github.wulkanowy.api.generic.Week<io.github.wulkanowy.api.generic.Day> getWeekFromApi(String date)
            throws IOException, NotLoggedInErrorException, ParseException {
        return vulcan.getAttendanceTable().getWeekTable(date);
    }

    private Week getWeekFromDb(String date) {
        return daoSession.getWeekDao()
                .queryBuilder()
                .where(WeekDao.Properties.UserId.eq(userId), WeekDao.Properties.StartDayDate.eq(date))
                .unique();
    }

    private Long updateWeekInDb(Week fromDb, io.github.wulkanowy.api.generic.Week fromApi) {
        if (fromDb != null) {
            fromDb.setIsAttendanceSynced(true);
            fromDb.update();

            return fromDb.getId();
        }

        Week weekFromNetEntity = DataObjectConverter.weekToWeekEntity(fromApi).setUserId(userId);
        weekFromNetEntity.setIsAttendanceSynced(true);

        return daoSession.getWeekDao().insert(weekFromNetEntity);
    }

    private List<AttendanceLesson> updateDays(List<io.github.wulkanowy.api.generic.Day> dayListFromApi, long weekId) {
        List<AttendanceLesson> updatedLessonList = new ArrayList<>();

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
        if (null != dayFromDb) {
            return dayFromDb.getId();
        }

        dayFromApiEntity.setUserId(userId);
        dayFromApiEntity.setWeekId(weekId);

        return daoSession.getDayDao().insert(dayFromApiEntity);
    }

    private void updateLessons(List<Lesson> lessons, List<AttendanceLesson> updatedLessons, long dayId) {
        List<AttendanceLesson> lessonsFromApiEntities = DataObjectConverter
                .lessonsToAttendanceLessonsEntities(lessons);

        for (AttendanceLesson lessonFromApiEntity : lessonsFromApiEntities) {
            AttendanceLesson lessonFromDb = getLessonFromDb(lessonFromApiEntity, dayId);

            lessonFromApiEntity.setDayId(dayId);

            if (lessonFromDb != null) {
                lessonFromApiEntity.setId(lessonFromDb.getId());
            }

            if (!"".equals(lessonFromApiEntity.getSubject())) {
                updatedLessons.add(lessonFromApiEntity);
            }
        }
    }

    private AttendanceLesson getLessonFromDb(AttendanceLesson lessonFromNetEntity, long dayId) {
        return daoSession.getAttendanceLessonDao().queryBuilder()
                .where(AttendanceLessonDao.Properties.DayId.eq(dayId),
                        AttendanceLessonDao.Properties.Date.eq(lessonFromNetEntity.getDate()),
                        AttendanceLessonDao.Properties.Number.eq(lessonFromNetEntity.getNumber()))
                .unique();
    }
}
