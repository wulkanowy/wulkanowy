package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.dao.AttendanceSummaryDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryRepository @Inject constructor(
    private val attendanceDb: AttendanceSummaryDao,
    private val sdk: Sdk,
    private val sharedPref: SharedPrefProvider,
) {

    private val key = "attendance_summary"

    fun getAttendanceSummary(student: Student, semester: Semester, subjectId: Int, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh || sharedPref.isShouldBeRefreshed(getRefreshKey(key, semester)) },
        query = { attendanceDb.loadAll(semester.diaryId, semester.studentId, subjectId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getAttendanceSummary(subjectId)
                .mapToEntities(semester, subjectId)
        },
        saveFetchResult = { old, new ->
            attendanceDb.deleteAll(old uniqueSubtract new)
            attendanceDb.insertAll(new uniqueSubtract old)
            sharedPref.updateLastRefreshTimestamp(getRefreshKey(key, semester))
        }
    )
}
