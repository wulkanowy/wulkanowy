package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe

@Dao
interface SemesterDao {

    @Insert(onConflict = IGNORE)
    fun insertAll(semester: List<Semester>)

    @Query("SELECT * FROM Semesters WHERE student_id = :studentId")
    fun getSemester(studentId: Int): Maybe<List<Semester>>

    @Query("UPDATE Semesters SET is_current = 0")
    fun resetCurrentSemester()

    @Query("UPDATE Semesters SET is_current = 1 WHERE semester_id = :semesterId")
    fun setCurrentSemester(semesterId: Int)
}
