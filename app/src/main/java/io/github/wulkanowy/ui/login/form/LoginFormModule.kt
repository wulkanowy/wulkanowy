package io.github.wulkanowy.ui.login.form

import dagger.Module
import dagger.Provides
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.reactivex.disposables.CompositeDisposable

@Module
internal class LoginFormModule {

    @Provides
    fun provideLoginFormPresenter(disposable: CompositeDisposable, schedulers: SchedulersManager,
                                  repository: StudentRepository): LoginFormPresenter {
        return LoginFormPresenter(disposable, schedulers, repository)
    }
}