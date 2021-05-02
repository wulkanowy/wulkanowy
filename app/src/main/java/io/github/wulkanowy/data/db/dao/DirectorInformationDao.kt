package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.DirectorInformation
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
@Singleton
interface DirectorInformationDao : BaseDao<DirectorInformation> {

    @Query("SELECT * FROM DirectorInformation WHERE student_id = :studentId")
    fun loadAll(studentId: Int): Flow<List<DirectorInformation>>
}
