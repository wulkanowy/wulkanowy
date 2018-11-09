package io.github.wulkanowy.ui.modules.note

import io.github.wulkanowy.ui.base.BaseView

interface NoteView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<NoteItem>)

    fun clearData()

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun hideRefresh()
}
