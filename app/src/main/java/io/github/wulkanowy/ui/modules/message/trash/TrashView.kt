package io.github.wulkanowy.ui.modules.message.trash

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.message.MessageItem

interface TrashView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<MessageItem>)

    fun updateItem(item: AbstractFlexibleItem<*>)

    fun clearView()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showRefresh(show: Boolean)

    fun showMessage(message: Message)

    fun notifyParentDataLoaded()
}
