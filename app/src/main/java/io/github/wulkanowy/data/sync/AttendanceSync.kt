package io.github.wulkanowy.data.sync

import io.github.wulkanowy.api.Vulcan
import io.github.wulkanowy.data.db.dao.entities.AttendanceLessonDao
import io.github.wulkanowy.data.db.dao.entities.DaoSession
import io.github.wulkanowy.utils.getAppDateFormatter
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.api.attendance.AttendanceLesson as ApiLesson
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson as EntityLesson

@Singleton
class AttendanceSync @Inject constructor(private val daoSession: DaoSession, private val vulcan: Vulcan) {

    fun syncAttendance(diaryId: Long, date: LocalDate) {
        val lessonList = getLessonListToSave(vulcan.attendance.getAttendance(date.format(getAppDateFormatter())), diaryId)

        daoSession.attendanceLessonDao.saveInTx(lessonList)

        Timber.d("Attendance synchronization complete (%s)", lessonList.size)
    }

    private fun getLessonListToSave(lessons: List<ApiLesson>, diaryId: Long): List<EntityLesson> {
        return lessons.map {
            EntityLesson(getLessonFromDb(it.date, it.number, diaryId)?.id, diaryId, it.date,
                    it.number, it.subject, it.presence, it.absenceUnexcused,
                    it.absenceExcused, it.unexcusedLateness, it.absenceForSchoolReasons,
                    it.excusedLateness, it.exemption)
        }
    }

    private fun getLessonFromDb(date: String, number: Int, diaryId: Long): EntityLesson? {
        return daoSession.attendanceLessonDao.queryBuilder()
                .where(AttendanceLessonDao.Properties.DiaryId.eq(diaryId),
                        AttendanceLessonDao.Properties.Date.eq(date),
                        AttendanceLessonDao.Properties.Number.eq(number))
                .unique()
    }
}
