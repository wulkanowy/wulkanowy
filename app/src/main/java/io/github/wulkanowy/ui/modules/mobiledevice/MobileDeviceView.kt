package io.github.wulkanowy.ui.modules.mobiledevice

import io.github.wulkanowy.ui.base.session.BaseSessionView

interface MobileDeviceView : BaseSessionView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<MobileDeviceItem>)

    fun showEmpty(show: Boolean)
}
