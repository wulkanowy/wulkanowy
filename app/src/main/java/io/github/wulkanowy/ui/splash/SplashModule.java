package io.github.wulkanowy.ui.splash;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class SplashModule {

    @Binds
    abstract SplashContract.Presenter provideSplashPresenter(SplashPresenter splashPresenter);
}
