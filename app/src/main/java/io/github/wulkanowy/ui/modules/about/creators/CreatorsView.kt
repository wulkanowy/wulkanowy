package io.github.wulkanowy.ui.modules.about.creators

import com.mikepenz.aboutlibraries.entity.Library
import io.github.wulkanowy.ui.base.BaseView

interface CreatorsView : BaseView {

    val appLibraries: ArrayList<Library>?

    fun initView()

    fun updateData(data: List<CreatorsItem>)

    fun openLicense(licenseHtml: String)

    fun showProgress(show: Boolean)
}
