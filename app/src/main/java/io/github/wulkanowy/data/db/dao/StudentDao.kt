package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface StudentDao {

    @Insert
    fun insert(student: Student): Long

    @Query("SELECT * FROM Students WHERE id = :id")
    fun load(id: Long): Maybe<Student>

    @Query("SELECT * FROM Students")
    fun loadAll(): Single<List<Student>>
}
