package io.github.wulkanowy.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.db.dao.DaoAccess;
import io.github.wulkanowy.db.dao.DaoHelper;
import io.github.wulkanowy.db.shared.SharedAccess;
import io.github.wulkanowy.db.shared.SharedHelper;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;
import io.github.wulkanowy.di.annotations.SharedPreferencesInfo;

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
        return DatabaseManager.DATABASE_NAME;
    }

    @SharedPreferencesInfo
    @Provides
    String provideSharedPreferencesName() {
        return DatabaseManager.SHARED_PREFERNCES_NAME;
    }

    /*
    @Singleton
    @Provides
    DatabaseManager provideDatabaseManager(DatabaseManager databaseManager){
        return databaseManager;
    }
    */

    @Singleton
    @Provides
    DaoAccess provideDaoAccess(DaoHelper daoHelper) {
        return daoHelper;
    }

    @Singleton
    @Provides
    SharedAccess provideSharedAccess(SharedHelper sharedHelper) {
        return sharedHelper;
    }
}
