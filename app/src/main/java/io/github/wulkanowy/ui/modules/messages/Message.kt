package io.github.wulkanowy.ui.modules.messages

import com.stfalcon.chatkit.commons.models.IMessage
import io.github.wulkanowy.ui.modules.messages.User
import java.util.*

data class Message(

        private val id: String,

        private val text: String?,

        private val createdAt: Date?,

        private val user: User

) : IMessage {

    override fun getId() = id

    override fun getText() = text

    override fun getCreatedAt() = createdAt

    override fun getUser() = user
}
