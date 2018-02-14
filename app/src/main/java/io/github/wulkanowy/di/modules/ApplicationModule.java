package io.github.wulkanowy.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.Repository;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.DbHelper;
import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.resources.AppResources;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.db.shared.SharedPref;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.LoginSync;
import io.github.wulkanowy.data.sync.LoginSyncContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;
import io.github.wulkanowy.di.annotations.SharedPreferencesInfo;
import io.github.wulkanowy.utils.AppConstant;

@Module
public class ApplicationModule {

    private final Application application;

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
    DaoSession provideDaoSession(DbHelper dbHelper) {
        return new DaoMaster(dbHelper.getWritableDb()).newSession();
    }

    @Provides
    Vulcan provideVulcan() {
        return new Vulcan();
    }

    @Singleton
    @Provides
    RepositoryContract provideRepository(Repository repository) {
        return repository;
    }

    @Singleton
    @Provides
    SharedPrefContract provideSharedPref(SharedPref sharedPref) {
        return sharedPref;
    }

    @Singleton
    @Provides
    ResourcesContract provideAppResources(AppResources appResources) {
        return appResources;
    }

    @Singleton
    @Provides
    LoginSyncContract provideLoginSync(LoginSync loginSync) {
        return loginSync;
    }
}
