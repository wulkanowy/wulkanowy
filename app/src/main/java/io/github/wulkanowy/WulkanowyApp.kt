package io.github.wulkanowy

import android.content.Context
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.utils.Log
import io.fabric.sdk.android.Fabric
import io.github.wulkanowy.BuildConfig.DEBUG
import io.github.wulkanowy.di.AppComponent
import io.github.wulkanowy.di.DaggerAppComponent
import io.github.wulkanowy.utils.CrashlyticsTree
import io.github.wulkanowy.utils.DebugLogTree
import timber.log.Timber
import io.github.wulkanowy.di.Provider

class WulkanowyApp : DaggerApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        initializeFabric()
        if (DEBUG) enableDebugLog()
    }

    private fun enableDebugLog() {
        Timber.plant(DebugLogTree)
        FlexibleAdapter.enableLogs(Log.Level.DEBUG)
    }

    private fun initializeFabric() {
        Fabric.with(Fabric.Builder(this)
                .kits(Crashlytics.Builder()
                        .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                        .build(),
                        Answers())
                .debuggable(BuildConfig.DEBUG)
                .build())
        Timber.plant(CrashlyticsTree)
    }

    // TODO: Update when [this issue](https://github.com/google/dagger/issues/1183) will be closed
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent.builder().create(this)
        appComponent.inject(this)
        Provider.appComponent = appComponent as AppComponent
        return appComponent
    }
}
