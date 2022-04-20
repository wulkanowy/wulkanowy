package io.github.wulkanowy.utils

import android.content.Context
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Suppress("unused")
class AdsHelper @Inject constructor(@ApplicationContext private val context: Context) {

    @Suppress("RedundantSuspendModifier")
    suspend fun getDashboardTileAdBanner(): AdBanner {
        throw IllegalStateException("Can't get ad banner (HMS)")
    }
}

data class AdBanner(val view: View)
