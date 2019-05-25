package io.github.wulkanowy.utils.logger

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import io.github.wulkanowy.BuildConfig.CRASHLYTICS_ENABLED
import io.github.wulkanowy.BuildConfig.DEBUG

fun initCrashlytics(context: Context) {
    Fabric.with(Fabric.Builder(context).kits(
        Crashlytics.Builder().core(CrashlyticsCore.Builder().disabled(!CRASHLYTICS_ENABLED).build()).build()
    ).debuggable(DEBUG).build())
}
