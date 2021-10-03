package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import io.github.wulkanowy.data.db.entities.AdminMessage
import javax.inject.Singleton

@Singleton
@Dao
interface AdminMessageDao : BaseDao<AdminMessage> {
}