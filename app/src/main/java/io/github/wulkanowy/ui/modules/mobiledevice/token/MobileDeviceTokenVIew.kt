package io.github.wulkanowy.ui.modules.mobiledevice.token

import io.github.wulkanowy.data.repositories.mobiledevice.MobileDeviceRemote
import io.github.wulkanowy.ui.base.BaseView

interface MobileDeviceTokenVIew : BaseView {

    fun initView()

    fun hideLoading()

    fun showContent()

    fun updateData(token: MobileDeviceRemote.Token)
}
