package io.github.wulkanowy.ui.main.grades;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import eu.davidea.flexibleadapter.FlexibleAdapter;

@Module
public abstract class GradesModule {

    @Binds
    abstract GradesContract.Presenter provideGradesPresenter(GradesPresenter gradesPresenter);

    @Provides
    static FlexibleAdapter<GradeHeaderItem> provideGradesAdapter() {
        return new FlexibleAdapter<>(null);
    }
}
