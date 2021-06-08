package io.github.wulkanowy.data.pojos

import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import io.github.wulkanowy.ui.modules.main.MainView

data class Notification(
    val channelId: String,
    @DrawableRes val icon: Int,
    @PluralsRes val titleStringRes: Int,
    @PluralsRes val contentStringRes: Int,
    @PluralsRes val summaryStringRes: Int,
    val startMenu: MainView.Section,
    val lines: List<String>,
)
