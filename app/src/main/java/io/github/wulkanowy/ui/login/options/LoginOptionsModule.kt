package io.github.wulkanowy.ui.login.options

import dagger.Module
import dagger.Provides
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.di.scopes.PerChildFragment
import io.github.wulkanowy.ui.login.LoginPresenter

@Module
internal class LoginOptionsModule {

    @Provides
    @PerChildFragment
    fun provideLoginPresenter(errorHandler: ErrorHandler): LoginPresenter {
        return LoginPresenter(errorHandler)
    }
}