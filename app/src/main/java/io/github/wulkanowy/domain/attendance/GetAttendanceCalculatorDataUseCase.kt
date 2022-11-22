package io.github.wulkanowy.domain.attendance

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.AttendanceData
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.SubjectRepository
import io.github.wulkanowy.utils.allAbsences
import io.github.wulkanowy.utils.allPresences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GetAttendanceCalculatorDataUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
) {

    suspend operator fun invoke(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
    ): Flow<Resource<List<AttendanceData>>> {
        val subjects = subjectRepository.getSubjects(student, semester, forceRefresh)
            .onResourceError { throw it }.toFirstResult().dataOrNull!!

        return combine(subjects.map { subject ->
            attendanceSummaryRepository.getAttendanceSummary(
                student = student,
                semester = semester,
                subjectId = subject.realId,
                forceRefresh = forceRefresh
            ).mapResourceData { summaries ->
                AttendanceData(subjectName = subject.name,
                    allPresences = summaries.sumOf { it.allPresences },
                    allAbsences = summaries.sumOf { it.allAbsences })
            }
        }) { items ->
            val data = mutableListOf<AttendanceData>()
            var isIntermediate = false
            for (item in items) {
                when (item) {
                    is Resource.Success -> data.add(item.data)
                    is Resource.Intermediate -> {
                        isIntermediate = true
                        data.add(item.data)
                    }
                    is Resource.Loading -> return@combine Resource.Loading()
                    is Resource.Error -> continue
                }
            }
            if (data.isEmpty()) {
                // All items have to be errors for this to happen, so just return the first one.
                // mapData is functionally useless and exists only to satisfy the type checker
                items.first().mapData { listOf(it) }
            } else if (isIntermediate) {
                Resource.Intermediate(data)
            } else {
                Resource.Success(data)
            }
        }.mapResourceData { it.sortedBy(AttendanceData::subjectName) }
            .distinctUntilChanged { old, new ->
                // Every individual combined flow causes separate network requests to update data.
                // When there is N child flows, they can cause up to N-1 items to be emitted. Since all
                // requests are usually completed in less than 5s, there is no need to emit multiple
                // intermediates that will be visible for barely any time.
                old is Resource.Intermediate && new is Resource.Intermediate
            }
    }
}
