package io.github.wulkanowy.ui.modules.about.creators

import io.github.wulkanowy.ui.base.BaseView

interface CreatorsView : BaseView {

    val appCreators: List<Creator>

    fun initView()

    fun updateData(data: List<CreatorsItem>)

    fun openLicense(licenseHtml: String)

    fun showProgress(show: Boolean)
}
