package io.github.wulkanowy.ui.modules.luckynumber

import io.github.wulkanowy.ui.base.BaseView

interface LuckyNumberSettingsView : BaseView {

    fun initView()

    fun updateData(allNotifications: Boolean, selfNotifications: Boolean, registerNumber: Int?)
}
