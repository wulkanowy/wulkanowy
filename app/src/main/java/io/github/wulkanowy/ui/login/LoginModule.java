package io.github.wulkanowy.ui.login;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class LoginModule {

    @Binds
    abstract LoginContract.Presenter provideLoginPresenter(LoginPresenter loginPresenter);
}
