package io.github.wulkanowy.ui.modules.messages.dialogs

import com.stfalcon.chatkit.commons.models.IDialog
import io.github.wulkanowy.ui.modules.messages.Message
import io.github.wulkanowy.ui.modules.messages.User

data class Dialog(
        private val id: String,
        private val dialogPhoto: String?,
        private val dialogName: String?,
        private val users: ArrayList<User>,
        private var lastMessage: Message?,
        private val unreadCount: Int
) : IDialog<Message> {

    override fun getId() = id

    override fun getDialogName() = dialogName

    override fun getDialogPhoto() = dialogPhoto

    override fun getUnreadCount() = unreadCount

    override fun getUsers() = users

    override fun getLastMessage() = lastMessage

    override fun setLastMessage(message: Message) {
        this.lastMessage = message
    }
}
