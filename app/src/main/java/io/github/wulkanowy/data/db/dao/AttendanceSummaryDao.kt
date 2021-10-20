package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.AttendanceSummaryWithName
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceSummaryDao : BaseDao<AttendanceSummary> {

    @Query("""SELECT subjects.name, AttendanceSummary.* FROM AttendanceSummary 
        INNER JOIN Subjects ON subjects.real_id = AttendanceSummary.subject_id
        WHERE AttendanceSummary.diary_id = :diaryId AND AttendanceSummary.student_id = :studentId""")
    fun loadAllWithName(diaryId: Int, studentId: Int): Flow<List<AttendanceSummaryWithName>>

    @Query("SELECT * FROM AttendanceSummary WHERE diary_id = :diaryId AND student_id = :studentId AND subject_id = :subjectId")
    fun loadAll(diaryId: Int, studentId: Int, subjectId: Int): Flow<List<AttendanceSummary>>
}
