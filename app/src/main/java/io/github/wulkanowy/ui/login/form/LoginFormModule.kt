package io.github.wulkanowy.ui.login.form

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.login.LoginErrorHandler
import io.github.wulkanowy.utils.schedulers.SchedulersManager

@Module
internal class LoginFormModule {

    @Provides
    fun provideLoginFormPresenter(schedulers: SchedulersManager, repository: StudentRepository,
                                  errorHandler: LoginErrorHandler): LoginFormPresenter {
        return LoginFormPresenter(schedulers, errorHandler, repository)
    }

    @Provides
    fun provideLoginErrorHandler(context: Context) = LoginErrorHandler(context.resources)
}