package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe

@Dao
interface StudentDao {

    @Insert
    fun insert(student: Student): Long

    @Query("SELECT * FROM Students WHERE is_current = 1")
    fun loadCurrent(): Maybe<Student>

    @Query("SELECT * FROM Students")
    fun loadAll(): Maybe<List<Student>>
}
