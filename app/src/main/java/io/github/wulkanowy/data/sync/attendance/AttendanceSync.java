package io.github.wulkanowy.data.sync.attendance;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.generic.Day;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLessonDao;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
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

    private final Vulcan vulcan;

    private final SharedPrefContract sharedPref;

    @Inject
    AttendanceSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void syncAttendance(String date) throws IOException, NotLoggedInErrorException, ParseException {
        long userId = sharedPref.getCurrentUserId();

        io.github.wulkanowy.api.generic.Week<io.github.wulkanowy.api.generic.Day> weekFromNet = date == null
                ? vulcan.getAttendanceTable().getWeekTable()
                : vulcan.getAttendanceTable().getWeekTable(String.valueOf(TimeUtils.getNetTicks(date)));

        Week weekFromDb = daoSession.getWeekDao().queryBuilder()
                .where(WeekDao.Properties.UserId.eq(userId),
                        WeekDao.Properties.StartDayDate.eq(weekFromNet.getStartDayDate()))
                .unique();

        Long weekId;

        if (weekFromDb == null) {
            Week weekFromNetEntity = DataObjectConverter.weekToWeekEntity(weekFromNet).setUserId(userId);
            weekId = daoSession.getWeekDao().insert(weekFromNetEntity);
        } else {
            weekId = weekFromDb.getId();
        }

        List<Day> dayListFromNet = weekFromNet.getDays();

        List<AttendanceLesson> updatedLessonList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Day dayFromNet : dayListFromNet) {
            io.github.wulkanowy.data.db.dao.entities.Day dayFromNetEntity = DataObjectConverter.dayToDayEntity(dayFromNet);

            io.github.wulkanowy.data.db.dao.entities.Day dayFromDb = daoSession.getDayDao().queryBuilder()
                    .where(DayDao.Properties.UserId.eq(userId),
                            DayDao.Properties.Date.eq(dayFromNetEntity.getDate()))
                    .unique();

            dayFromNetEntity.setUserId(userId);
            dayFromNetEntity.setWeekId(weekId);

            Long dayId;

            if (dayFromDb != null) {
                dayFromNetEntity.setId(dayFromDb.getId());
                daoSession.getDayDao().save(dayFromNetEntity);
                dayId = dayFromNetEntity.getId();
            } else {
                dayId = daoSession.getDayDao().insert(dayFromNetEntity);
            }

            List<AttendanceLesson> lessonListFromNetEntities = DataObjectConverter
                    .lessonsToAttendanceLessonsEntities(dayFromNet.getLessons());

            for (AttendanceLesson lessonFromNetEntity : lessonListFromNetEntities) {
                AttendanceLesson lessonFromDb = daoSession.getAttendanceLessonDao().queryBuilder()
                        .where(AttendanceLessonDao.Properties.DayId.eq(dayId),
                                AttendanceLessonDao.Properties.Date.eq(lessonFromNetEntity.getDate()),
                                AttendanceLessonDao.Properties.Number.eq(lessonFromNetEntity.getNumber()))
                        .unique();

                if (lessonFromDb != null) {
                    lessonFromNetEntity.setId(lessonFromDb.getId());
                }

                lessonFromNetEntity.setDayId(dayFromNetEntity.getId());

                if (!"".equals(lessonFromNetEntity.getSubject())) {
                    updatedLessonList.add(lessonFromNetEntity);
                }
            }
        }
        daoSession.getAttendanceLessonDao().saveInTx(updatedLessonList);

        LogUtils.debug("Synchronization lessons (amount = " + updatedLessonList.size() + ")");
    }

    @Override
    public void syncAttendance() throws IOException, NotLoggedInErrorException, ParseException {
        syncAttendance(null);
    }
}
