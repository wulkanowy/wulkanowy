package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.reactivex.Single

@Dao
interface MessageAttachmentDao : BaseDao<MessageAttachment> {

    @Query("SELECT * FROM MessageAttachments WHERE message_id = :messageId")
    fun loadAll(messageId: Int): Single<List<MessageAttachment>>
}
