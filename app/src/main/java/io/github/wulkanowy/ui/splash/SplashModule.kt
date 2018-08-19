package io.github.wulkanowy.ui.splash

import dagger.Module
import dagger.Provides
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.di.scopes.PerActivity
import io.reactivex.disposables.CompositeDisposable

@Module
internal class SplashModule {

    @PerActivity
    @Provides
    fun provideSplashPresenter(disposable: CompositeDisposable, repository: StudentRepository): SplashPresenter {
        return SplashPresenter(repository, disposable)
    }
}
