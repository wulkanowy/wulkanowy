package io.github.wulkanowy.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.wulkanowy.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AdModule {
    @Singleton
    @Provides
    fun provideAdsHelper(@ApplicationContext appContext: Context): AdsHelper =
        if (BuildConfig.DEBUG) DebugAdsHelper(appContext) else DisabledAdsHelper
}


class DebugAdsHelper(
    private val context: Context,
) : AdsHelper {

    override val isMobileAdsSdkInitialized = MutableStateFlow(true)
    override val canShowAd = true

    @SuppressLint("SetTextI18n")
    override suspend fun getDashboardTileAdBanner(width: Int): AdBanner {
        return AdBanner(TextView(context).apply {
            text = "AD BANNER"
        })
    }

    override fun openAgreements() {}

    override suspend fun getSupportAd(): SupportAd = SupportAdImpl()
}

private class SupportAdImpl : SupportAd {
    override fun show(activity: Activity) {
        Toast.makeText(activity, "Ads not supported", Toast.LENGTH_SHORT).show()
    }
}
