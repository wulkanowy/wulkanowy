package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Preference
import javax.inject.Singleton

@Dao
@Singleton
interface PreferenceDao : BaseDao<Preference> {

    @Insert(onConflict = REPLACE)
    suspend fun putPreference(preference: Preference)

    @Query("SELECT * FROM Preferences WHERE studentId = :studentId AND `key` = :key")
    suspend fun getPreference(studentId: Int, key: String): Preference?
}
