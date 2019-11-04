package io.github.wulkanowy.data.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import java.io.Serializable

interface BaseDao<in T : Serializable> {

    @Insert
    fun insertAll(items: List<T>): List<Long>

    @Update
    fun updateAll(items: List<T>)

    @Delete
    fun deleteAll(items: List<T>)
}
