package io.github.wulkanowy.ui.main.more

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class MorePresenter @Inject constructor(errorHandler: ErrorHandler)
    : BasePresenter<MoreView>(errorHandler) {

    override fun onAttachView(view: MoreView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is MoreItem) {
            view?.openSettingsView()
        }
    }

    private fun loadData() {
        view?.run { settingsRes()?.let { updateData(listOf(MoreItem(it.first, it.second))) } }
    }
}
