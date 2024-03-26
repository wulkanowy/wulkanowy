package io.github.wulkanowy.utils

import android.app.Activity
import android.view.View
import io.github.wulkanowy.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DisabledAdsHelper : AdsHelper {
    override val supportsAds: Boolean
        get() = false
    override val isMobileAdsSdkInitialized = MutableStateFlow(false)
    override val canShowAd: Boolean = false

    override suspend fun getDashboardTileAdBanner(width: Int): AdBanner {
        throw IllegalStateException("Can't get ad banner (${BuildConfig.FLAVOR})")
    }

    override suspend fun getSupportAd() = null
}

interface AdsHelper {
    val supportsAds: Boolean
        get() = true

    val isMobileAdsSdkInitialized: StateFlow<Boolean>
    val canShowAd: Boolean

    fun initialize() {}
    suspend fun getDashboardTileAdBanner(width: Int): AdBanner
    fun openAgreements() {}
    suspend fun getSupportAd(): SupportAd? = null

}

data class AdBanner(val view: View)

interface SupportAd {
    fun show(activity: Activity)
}
