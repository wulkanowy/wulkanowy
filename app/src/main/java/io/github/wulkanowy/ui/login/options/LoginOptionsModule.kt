package io.github.wulkanowy.ui.login.options

import dagger.Module
import dagger.Provides
import io.github.wulkanowy.di.scopes.PerChildFragment
import io.github.wulkanowy.ui.login.LoginPresenter
import io.reactivex.disposables.CompositeDisposable

@Module
internal class LoginOptionsModule {

    @Provides
    @PerChildFragment
    fun provideLoginPresenter(disposable: CompositeDisposable): LoginPresenter {
        return LoginPresenter(disposable)
    }
}