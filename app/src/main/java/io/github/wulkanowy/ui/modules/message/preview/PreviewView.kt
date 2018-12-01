package io.github.wulkanowy.ui.modules.message.preview

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.BaseView

interface PreviewView : BaseView {

    fun setData(message: Message)

    fun showProgress(show: Boolean)
}
