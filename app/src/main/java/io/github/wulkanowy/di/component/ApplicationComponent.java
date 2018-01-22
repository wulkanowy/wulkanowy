package io.github.wulkanowy.di.component;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import io.github.wulkanowy.WulkanowyApp;
import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.modules.ApplicationModule;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(WulkanowyApp wulkanowyApp);

    @ApplicationContext
    Context getContext();

    DatabaseManager getDataManager();

}
