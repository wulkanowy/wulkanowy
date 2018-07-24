package io.github.wulkanowy.data.sync

import io.github.wulkanowy.api.Vulcan
import io.github.wulkanowy.data.db.dao.entities.DaoSession
import io.github.wulkanowy.data.db.dao.entities.TimetableLessonDao
import io.github.wulkanowy.utils.getAppDateFormatter
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.api.timetable.TimetableLesson as ApiLesson
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson as EntityLesson

@Singleton
class TimetableSync @Inject constructor(private val daoSession: DaoSession, private val vulcan: Vulcan) {

    fun syncTimetable(diaryId: Long, date: LocalDate) {
        val lessonList = getLessonListToSave(vulcan.timetable.getTimetable(date.format(getAppDateFormatter())), diaryId)

        daoSession.timetableLessonDao.saveInTx(lessonList)

        Timber.d("Timetable synchronization complete (%s)", lessonList.size)
    }

    private fun getLessonListToSave(lessons: List<ApiLesson>, diaryId: Long): List<EntityLesson> {
        return lessons.map {
            EntityLesson(getLessonFromDb(it.date, it.number, diaryId)?.id, diaryId, it.number,
                    it.subject, it.teacher, it.room, it.description, it.groupName, it.startTime,
                    it.endTime, it.date, it.freeDayName, it.empty, it.divisionIntoGroups, it.planning,
                    it.realized, it.movedOrCanceled, it.newMovedInOrChanged)
        }
    }

    private fun getLessonFromDb(date: String, number: Int, diaryId: Long): EntityLesson? {
        return daoSession.timetableLessonDao.queryBuilder()
                .where(TimetableLessonDao.Properties.DiaryId.eq(diaryId),
                        TimetableLessonDao.Properties.Date.eq(date),
                        TimetableLessonDao.Properties.Number.eq(number))
                .unique()
    }
}
