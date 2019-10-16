package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.SchoolInfo
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface SchoolInfoDao {

    @Insert
    fun insert(schoolInfo: SchoolInfo)

    @Delete
    fun delete(schoolInfo: SchoolInfo)

    @Query("SELECT * FROM SchoolInfo WHERE student_id = :studentId AND class_id = :classId")
    fun load(studentId: Int, classId: Int): Maybe<SchoolInfo>
}
