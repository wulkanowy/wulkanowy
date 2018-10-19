package io.github.wulkanowy.ui.main.more

import android.graphics.drawable.Drawable
import io.github.wulkanowy.ui.base.BaseView

interface MoreView : BaseView {

    fun initView()

    fun updateData(data: List<MoreItem>)

    fun openSettingsView()

    fun settingsRes(): Pair<String, Drawable?>?
}
