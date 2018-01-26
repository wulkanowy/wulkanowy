package io.github.wulkanowy.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.db.dao.AppDatabase;
import io.github.wulkanowy.db.dao.DaoHelper;
import io.github.wulkanowy.db.resources.AppResources;
import io.github.wulkanowy.db.resources.ResourcesHelper;
import io.github.wulkanowy.db.shared.AppShared;
import io.github.wulkanowy.db.shared.SharedHelper;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;
import io.github.wulkanowy.di.annotations.SharedPreferencesInfo;
import io.github.wulkanowy.utils.AppConstant;

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

    @DatabaseInfo
    @Provides
    String provideDatabaseName() {
        return AppConstant.DATABASE_NAME;
    }

    @SharedPreferencesInfo
    @Provides
    String provideSharedPreferencesName() {
        return AppConstant.SHARED_PREFERENCES_NAME;
    }

    @Singleton
    @Provides
    AppDatabase provideAppDatabase(DaoHelper daoHelper) {
        return daoHelper;
    }

    @Singleton
    @Provides
    AppShared provideAppShared(SharedHelper sharedHelper) {
        return sharedHelper;
    }

    @Singleton
    @Provides
    AppResources provideAppResources(ResourcesHelper resourcesHelper) {
        return resourcesHelper;
    }
}
