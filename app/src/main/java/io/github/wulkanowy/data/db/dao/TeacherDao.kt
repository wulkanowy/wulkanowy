package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Teacher
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface TeacherDao : BaseDao<Teacher> {

    @Query("SELECT * FROM Teachers WHERE student_id = :studentId AND class_id = :classId")
    fun loadAllWithClassId(studentId: Int, classId: Int): Flow<List<Teacher>>

    @Query("SELECT * FROM Teachers WHERE student_id = :studentId")
    fun loadAllNoClassId(studentId: Int): Flow<List<Teacher>>

    fun loadAll(student: Student): Flow<List<Teacher>> {
        return if (student.isEduOne == true) {
            loadAllNoClassId(student.studentId)
        } else {
            loadAllWithClassId(student.studentId, student.classId)
        }
    }
}
