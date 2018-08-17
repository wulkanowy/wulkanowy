package io.github.wulkanowy.ui.login.form

import dagger.Binds
import dagger.Module

@Module
internal abstract class LoginFormModule {

    @Binds
    abstract fun provideLoginFormPresenter(presenter: LoginFormPresenter): LoginFormContract.Presenter
}