package io.github.wulkanowy;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.greenrobot.greendao.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.HandroidLoggerAdapter;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.utils.Log;
import io.fabric.sdk.android.Fabric;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.di.DaggerAppComponent;
import io.github.wulkanowy.utils.AppConstant;
import io.github.wulkanowy.utils.FabricUtils;

public class WulkanowyApp extends DaggerApplication {

    @Inject
    RepositoryContract repository;

    private static final Logger logger = LoggerFactory.getLogger(WulkanowyApp.class);

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);

        if (BuildConfig.DEBUG) {
            enableDebugLog();
        }
        initializeFabric();
        initializeUserSession();
    }

    private void initializeUserSession() {
        if (repository.getSharedRepo().isUserLoggedIn()) {
            try {
                repository.getSyncRepo().initLastUser();
                FabricUtils.logLogin("Open app", true);
            } catch (Exception e) {
                FabricUtils.logLogin("Open app", false);
                logger.error("An error occurred when the application was started", e);
            }
        }
    }

    private void enableDebugLog() {
        QueryBuilder.LOG_VALUES = true;
        FlexibleAdapter.enableLogs(Log.Level.DEBUG);
        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG;
        HandroidLoggerAdapter.APP_NAME = AppConstant.APP_NAME;
    }

    private void initializeFabric() {
        Fabric.with(new Fabric.Builder(this)
                .kits(
                        new Crashlytics.Builder()
                                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                                .build(),
                        new Answers()
                )
                .debuggable(BuildConfig.DEBUG)
                .build());
        HandroidLoggerAdapter.enableLoggingToCrashlytics();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }
}
