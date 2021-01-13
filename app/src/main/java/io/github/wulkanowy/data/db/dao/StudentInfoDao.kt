package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.github.wulkanowy.data.db.entities.StudentInfo
import io.github.wulkanowy.data.db.entities.StudentInfoWithGuardians
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface StudentInfoDao : BaseDao<StudentInfo> {

    @Transaction
    @Query("SELECT * FROM StudentInfo WHERE student_id = :studentId")
    fun loadStudentInfoWithGuardians(studentId: Int): Flow<StudentInfoWithGuardians>
}
