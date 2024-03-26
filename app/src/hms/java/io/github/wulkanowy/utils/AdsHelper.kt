package io.github.wulkanowy.utils

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal class AdModule {
    @ActivityScoped
    @Provides
    fun provideAdsHelper(): AdsHelper = DisabledAdsHelper
}
