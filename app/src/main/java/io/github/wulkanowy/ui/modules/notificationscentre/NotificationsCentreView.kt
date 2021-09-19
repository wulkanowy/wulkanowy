package io.github.wulkanowy.ui.modules.notificationscentre

import io.github.wulkanowy.data.db.entities.Notification
import io.github.wulkanowy.ui.base.BaseView

interface NotificationsCentreView : BaseView {

    fun initView()

    fun updateData(data: List<Notification>)

    fun showProgress(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showContent(show: Boolean)
}