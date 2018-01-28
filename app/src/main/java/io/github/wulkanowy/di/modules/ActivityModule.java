package io.github.wulkanowy.di.modules;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.di.annotations.ActivityContext;
import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.login.LoginContract;
import io.github.wulkanowy.ui.login.LoginPresenter;
import io.github.wulkanowy.ui.login.LoginTask;
import io.github.wulkanowy.ui.main.MainContract;
import io.github.wulkanowy.ui.main.MainPresenter;
import io.github.wulkanowy.ui.splash.SplashContract;
import io.github.wulkanowy.ui.splash.SplashPresenter;

@Module
public class ActivityModule {

    protected Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @ActivityContext
    @Provides
    Context provideContext() {
        return activity;
    }

    @Provides
    Activity provideActivity() {
        return activity;
    }

    @PerActivity
    @Provides
    SplashContract.Presenter provideSplashPresenter
            (SplashPresenter splashPresenter) {
        return splashPresenter;
    }

    @PerActivity
    @Provides
    LoginContract.Presenter provideLoginPresenter
            (LoginPresenter loginPresenter) {
        return loginPresenter;
    }

    @PerActivity
    @Provides
    MainContract.Presenter provideMainPresenter
            (MainPresenter mainPresenter) {
        return mainPresenter;
    }

    @PerActivity
    @Provides
    LoginContract.Async provideLoginTask() {
        return new LoginTask();
    }
}
