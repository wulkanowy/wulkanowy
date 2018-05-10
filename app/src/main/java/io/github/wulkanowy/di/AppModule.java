package io.github.wulkanowy.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import io.github.wulkanowy.WulkanowyApp;
import io.github.wulkanowy.data.Repository;
import io.github.wulkanowy.data.RepositoryContract;

@Module
public abstract class AppModule {

    @Binds
    abstract Context provideContext(WulkanowyApp app);

    @Singleton
    @Binds
    abstract RepositoryContract provideRepository(Repository repository);
}
