package io.github.wulkanowy.ui.main;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import io.github.wulkanowy.di.scopes.PerActivity;
import io.github.wulkanowy.di.scopes.PerFragment;
import io.github.wulkanowy.ui.base.BasePagerAdapter;
import io.github.wulkanowy.ui.main.grades.GradesFragment;
import io.github.wulkanowy.ui.main.grades.GradesModule;
import io.github.wulkanowy.ui.main.timetable.TimetableFragment;
import io.github.wulkanowy.ui.main.timetable.TimetableModule;

@Module
public abstract class MainModule {

    @PerActivity
    @Binds
    abstract MainContract.Presenter provideMainPresenter(MainPresenter mainPresenter);

    @Named("Main")
    @PerActivity
    @Provides
    static BasePagerAdapter provideAdapter(MainActivity activity) {
        return new BasePagerAdapter(activity.getSupportFragmentManager());
    }

    @PerFragment
    @ContributesAndroidInjector(modules = GradesModule.class)
    abstract GradesFragment bindsGradesFragment();

    @PerFragment
    @ContributesAndroidInjector(modules = TimetableModule.class)
    abstract TimetableFragment bindTimetableFragment();
}
