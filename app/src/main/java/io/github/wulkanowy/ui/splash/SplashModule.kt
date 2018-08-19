package io.github.wulkanowy.ui.splash

import dagger.Module
import dagger.Provides
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.di.scopes.PerActivity

@Module
internal class SplashModule {

    @PerActivity
    @Provides
    fun provideSplashPresenter(repository: StudentRepository,
                               errorHandler: ErrorHandler): SplashPresenter {
        return SplashPresenter(repository, errorHandler)
    }
}
