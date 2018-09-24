package io.github.wulkanowy.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.wulkanowy.data.db.entities.Timetable
import io.reactivex.Maybe
import java.util.*

@Dao
interface TimetableDao {

    @Insert
    fun insertAll(exams: List<Timetable>): List<Long>

    @Delete
    fun deleteAll(exams: List<Timetable>)

    @Query("SELECT * FROM Timetable WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun getTimetable(diaryId: String, studentId: String, from: Date, end: Date): Maybe<List<Timetable>>
}
