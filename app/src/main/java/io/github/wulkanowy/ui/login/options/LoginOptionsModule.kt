package io.github.wulkanowy.ui.login.options

import dagger.Binds
import dagger.Module

@Module
internal abstract class LoginOptionsModule {

    @Binds
    abstract fun provideLoginPresenter(presenter: LoginOptionsPresenter): LoginOptionsContract.Presenter
}