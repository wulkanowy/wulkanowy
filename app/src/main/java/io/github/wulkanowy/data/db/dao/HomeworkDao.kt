package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.wulkanowy.data.db.entities.Homework
import io.reactivex.Maybe
import org.threeten.bp.LocalDate

@Dao
interface HomeworkDao {

    @Insert
    fun insertAll(homework: List<Homework>)

    @Update
    fun update(homework: Homework)

    @Update
    fun updateAll(homework: List<Homework>)

    @Delete
    fun deleteAll(homework: List<Homework>)

    @Query("SELECT * FROM Homework WHERE semester_id = :semesterId AND student_id = :studentId AND date = :date")
    fun getHomework(semesterId: Int, studentId: Int, date: LocalDate): Maybe<List<Homework>>

    @Query("SELECT * FROM Homework WHERE is_read = 0 AND semester_id = :semesterId AND student_id = :studentId")
    fun getNewHomework(semesterId: Int, studentId: Int): Maybe<List<Homework>>
}
