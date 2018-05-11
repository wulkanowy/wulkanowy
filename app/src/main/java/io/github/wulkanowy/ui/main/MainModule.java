package io.github.wulkanowy.ui.main;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.ui.base.BasePagerAdapter;

@Module
public abstract class MainModule {

    @Binds
    abstract MainContract.Presenter provideMainPresenter(MainPresenter mainPresenter);

    @Provides
    static BasePagerAdapter provideAdapter(MainActivity activity) {
        return new BasePagerAdapter(activity.getSupportFragmentManager());
    }
}
