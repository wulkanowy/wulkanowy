package io.github.wulkanowy.ui.modules.about.creator

import android.content.Context
import com.mikepenz.aboutlibraries.Libs
import dagger.Module
import dagger.Provides
import io.github.wulkanowy.di.scopes.PerFragment

@Module
class CreatorModule {

    @PerFragment
    @Provides
    fun provideLibs(context: Context) = Libs(context)
}
