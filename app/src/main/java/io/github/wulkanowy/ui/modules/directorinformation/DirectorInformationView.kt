package io.github.wulkanowy.ui.modules.directorinformation

import io.github.wulkanowy.data.db.entities.DirectorInformation
import io.github.wulkanowy.ui.base.BaseView

interface DirectorInformationView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<DirectorInformation>)

    fun clearData()

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showRefresh(show: Boolean)
}
