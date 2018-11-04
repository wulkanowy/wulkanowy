package io.github.wulkanowy.ui.modules.messages

import com.stfalcon.chatkit.commons.models.IUser

data class User(
        private val id: String,

        private val name: String?,

        private val avatar: String?
) : IUser {

    override fun getId() = id

    override fun getName() = name

    override fun getAvatar() = avatar
}
