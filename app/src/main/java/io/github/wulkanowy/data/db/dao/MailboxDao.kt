package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Mailbox
import javax.inject.Singleton

@Singleton
@Dao
interface MailboxDao : BaseDao<Mailbox> {

    @Query("SELECT * FROM Mailboxes WHERE email = :email")
    suspend fun loadAll(email: String): List<Mailbox>
}
