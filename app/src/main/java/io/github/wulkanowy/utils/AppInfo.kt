package io.github.wulkanowy.utils

import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.os.Build.VERSION.SDK_INT
import io.github.wulkanowy.BuildConfig.CRASHLYTICS_ENABLED
import io.github.wulkanowy.BuildConfig.DEBUG
import io.github.wulkanowy.BuildConfig.VERSION_CODE
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import javax.inject.Singleton

@Singleton
open class AppInfo {

    open val isCrashlyticsEnabled = CRASHLYTICS_ENABLED

    open val isDebug = DEBUG

    open val versionCode = VERSION_CODE

    open val versionName = VERSION_NAME

    open val systemVersion = SDK_INT

    open val systemManufacturer: String = MANUFACTURER

    open val systemModel: String = MODEL
}
