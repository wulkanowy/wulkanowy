package io.github.wulkanowy.ui.modules.mobiledevice

import io.github.wulkanowy.ui.base.BaseView

interface MobileDeviceView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<MobileDeviceItem>)

    fun showEmpty(show: Boolean)

    fun hideRefresh()

    fun clearData()

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showTokenDialog()
}
