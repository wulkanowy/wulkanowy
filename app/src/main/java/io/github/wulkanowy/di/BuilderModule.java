package io.github.wulkanowy.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.github.wulkanowy.ui.login.LoginActivity;
import io.github.wulkanowy.ui.login.LoginModule;
import io.github.wulkanowy.ui.main.MainActivity;
import io.github.wulkanowy.ui.main.MainModule;
import io.github.wulkanowy.ui.splash.SplashActivity;
import io.github.wulkanowy.ui.splash.SplashModule;

@Module
abstract class BuilderModule {

    @ContributesAndroidInjector(modules = SplashModule.class)
    abstract SplashActivity bindSplashActivity();

    @ContributesAndroidInjector(modules = LoginModule.class)
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity bindMainActivity();
}
