package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Student
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface SchoolDao : BaseDao<School> {

    @Query("SELECT * FROM School WHERE student_id = :studentId AND class_id = :classId")
    fun loadWithClassId(studentId: Int, classId: Int): Flow<School?>

    @Query("SELECT * FROM School WHERE student_id = :studentId")
    fun loadNoClassId(studentId: Int): Flow<School?>

    fun load(student: Student): Flow<School?> {
        return if (student.isEduOne == true) {
            loadNoClassId(student.studentId)
        } else {
            loadWithClassId(student.studentId, student.classId)
        }
    }
}
