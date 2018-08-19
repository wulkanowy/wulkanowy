package io.github.wulkanowy.ui.login

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerActivity
import io.github.wulkanowy.di.scopes.PerChildFragment
import io.github.wulkanowy.ui.base.BasePagerAdapter
import io.github.wulkanowy.ui.login.form.LoginFormFragment
import io.github.wulkanowy.ui.login.form.LoginFormModule
import io.github.wulkanowy.ui.login.options.LoginOptionsFragment
import io.github.wulkanowy.ui.login.options.LoginOptionsModule
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Named

@Module
internal abstract class LoginModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("Login")
        fun provideLoginAdapter(activity: LoginActivity) = BasePagerAdapter(activity.supportFragmentManager)

        @JvmStatic
        @Provides
        @PerActivity
        fun provideLoginPresenter(disposable: CompositeDisposable): LoginPresenter {
            return LoginPresenter(disposable)
        }
    }

    @PerChildFragment
    @ContributesAndroidInjector(modules = [LoginFormModule::class])
    abstract fun bindLoginFormFragment(): LoginFormFragment

    @PerChildFragment
    @ContributesAndroidInjector(modules = [LoginOptionsModule::class])
    abstract fun bindLoginOptionsFragment(): LoginOptionsFragment
}
