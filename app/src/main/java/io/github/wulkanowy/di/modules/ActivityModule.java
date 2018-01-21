package io.github.wulkanowy.di.modules;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.di.annotations.ActivityContext;

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
}
