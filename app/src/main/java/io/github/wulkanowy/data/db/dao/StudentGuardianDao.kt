package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import io.github.wulkanowy.data.db.entities.StudentGuardian
import javax.inject.Singleton

@Singleton
@Dao
interface StudentGuardianDao : BaseDao<StudentGuardian>