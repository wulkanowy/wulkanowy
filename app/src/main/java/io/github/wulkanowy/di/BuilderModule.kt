package io.github.wulkanowy.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerActivity
import io.github.wulkanowy.ui.login.LoginActivity
import io.github.wulkanowy.ui.login.LoginModule
import io.github.wulkanowy.ui.main.MainActivity
import io.github.wulkanowy.ui.splash.SplashActivity

@Module
internal abstract class BuilderModule {

    @PerActivity
    @ContributesAndroidInjector()
    abstract fun bindSplashActivity(): SplashActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun bindLoginActivity(): LoginActivity

    @PerActivity
    @ContributesAndroidInjector()
    abstract fun bindMainActivity(): MainActivity
}
/*
    @PerActivity
    @ContributesAndroidInjector(modules = LoginModule.class)
    abstract LoginActivity bindLoginActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector
    abstract SyncJob bindSyncJob();

    @ContributesAndroidInjector
    abstract TimetableWidgetServices bindTimetableWidgetServices();

    @ContributesAndroidInjector
    abstract TimetableWidgetProvider bindTimetableWidgetProvider();*/
