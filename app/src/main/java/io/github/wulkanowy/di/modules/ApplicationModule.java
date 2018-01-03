package io.github.wulkanowy.di.modules;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.di.annotations.ApplicationContext;

@Module
public class ApplicationModule {

    protected final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @ApplicationContext
    @Provides
    Context provideAppContext() {
        return application;
    }
}
