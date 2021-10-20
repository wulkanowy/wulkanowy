package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.dao.AttendanceSummaryDao
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryRepository @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val attendanceDb: AttendanceSummaryDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "attendance_summary"

    @OptIn(FlowPreview::class)
    fun getSumAttendanceSummaryWithName(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, semester))
            it.isEmpty() || forceRefresh || isExpired
        },
        query = {
            attendanceDb.loadAllWithName(semester.diaryId, semester.studentId)
        },
        fetch = {
            val subjects = subjectRepository.getSubjects(student, semester, forceRefresh)
                .onResourceError { throw it }.toFirstResult()
            subjects.dataOrNull!!.asFlow().flatMapMerge { subject ->
                getAttendanceSummary(
                    student,
                    semester,
                    subject.realId,
                    forceRefresh
                ).onResourceError { throw it }.takeWhile { it is Resource.Loading }
            }.collect()
        },
        saveFetchResult = { _, _ -> },
        mapResult = {
            val summaryBySubject = it.groupBy { it.name }
            summaryBySubject.mapValues { (_name, summaryWithNameList) ->
                summaryWithNameList.map { it.attendanceSummary }
            }.toList()
        }
    )

    fun getAttendanceSummary(
        student: Student,
        semester: Semester,
        subjectId: Int,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, semester))
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { attendanceDb.loadAll(semester.diaryId, semester.studentId, subjectId) },
        fetch = {
            sdk.init(student)
                .switchDiary(semester.diaryId, semester.kindergartenDiaryId, semester.schoolYear)
                .getAttendanceSummary(subjectId)
                .mapToEntities(semester, subjectId)
        },
        saveFetchResult = { old, new ->
            attendanceDb.deleteAll(old uniqueSubtract new)
            attendanceDb.insertAll(new uniqueSubtract old)
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, semester))
        }
    )
}
