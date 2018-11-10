package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.wulkanowy.data.db.entities.Note
import io.reactivex.Maybe

@Dao
interface NoteDao {

    @Insert
    fun insertAll(notes: List<Note>)

    @Update
    fun update(note: Note)

    @Delete
    fun deleteAll(notes: List<Note>)

    @Query("SELECT * FROM Notes WHERE semester_id = :semesterId AND student_id = :studentId")
    fun getNotes(semesterId: Int, studentId: Int): Maybe<List<Note>>
}
