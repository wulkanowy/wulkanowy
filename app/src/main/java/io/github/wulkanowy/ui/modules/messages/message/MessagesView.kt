package io.github.wulkanowy.ui.modules.messages.message

import com.stfalcon.chatkit.commons.models.IMessage
import io.github.wulkanowy.ui.base.BaseView

interface MessagesView : BaseView {

    fun addToEnd(message: List<IMessage>)

    fun setActivityTitle(senderName: String)

    fun setTotalMessages(total: Int)

    fun initAdapter()
}
