package io.github.wulkanowy.di.modules;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import dagger.Module;
import dagger.Provides;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.github.wulkanowy.di.annotations.ActivityContext;
import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.login.LoginContract;
import io.github.wulkanowy.ui.login.LoginPresenter;
import io.github.wulkanowy.ui.main.MainContract;
import io.github.wulkanowy.ui.main.MainPagerAdapter;
import io.github.wulkanowy.ui.main.MainPresenter;
import io.github.wulkanowy.ui.main.attendance.AttendanceContract;
import io.github.wulkanowy.ui.main.attendance.AttendancePresenter;
import io.github.wulkanowy.ui.main.dashboard.DashboardContract;
import io.github.wulkanowy.ui.main.dashboard.DashboardPresenter;
import io.github.wulkanowy.ui.main.grades.GradeHeaderItem;
import io.github.wulkanowy.ui.main.grades.GradesContract;
import io.github.wulkanowy.ui.main.grades.GradesPresenter;
import io.github.wulkanowy.ui.splash.SplashContract;
import io.github.wulkanowy.ui.splash.SplashPresenter;

@Module
public class ActivityModule {

    protected AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ActivityContext
    @Provides
    Context provideContext() {
        return activity;
    }

    @Provides
    AppCompatActivity provideActivity() {
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
    GradesContract.Presenter provideGradesPresenter(GradesPresenter gradesPresenter) {
        return gradesPresenter;
    }

    @PerActivity
    @Provides
    AttendanceContract.Presenter provideAttendancePresenter(AttendancePresenter attendancePresenter) {
        return attendancePresenter;
    }

    @PerActivity
    @Provides
    DashboardContract.Presenter provideDashboardPresenter(DashboardPresenter dashboardPresenter) {
        return dashboardPresenter;
    }

    @Provides
    MainPagerAdapter provideMainPagerAdapter() {
        return new MainPagerAdapter(activity.getSupportFragmentManager());
    }

    @Provides
    FlexibleAdapter<GradeHeaderItem> provideGradesAdapter() {
        return new FlexibleAdapter<>(null);
    }
}
