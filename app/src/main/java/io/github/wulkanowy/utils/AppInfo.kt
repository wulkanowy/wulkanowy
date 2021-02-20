package io.github.wulkanowy.utils

import android.content.res.Resources
import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.os.Build.VERSION.SDK_INT
import io.github.wulkanowy.BuildConfig.BUILD_TIMESTAMP
import io.github.wulkanowy.BuildConfig.DEBUG
import io.github.wulkanowy.BuildConfig.FLAVOR
import io.github.wulkanowy.BuildConfig.VERSION_CODE
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppInfo @Inject constructor() {

    open val isDebug get() = DEBUG

    open val versionCode get() = VERSION_CODE

    open val buildTimestamp get() = BUILD_TIMESTAMP

    open val buildFlavor get() = FLAVOR

    open val versionName get() = VERSION_NAME

    open val systemVersion get() = SDK_INT

    open val systemManufacturer: String get() = MANUFACTURER

    open val systemModel: String get() = MODEL

    @Suppress("DEPRECATION")
    open val systemLanguage: String
        get() = Resources.getSystem().configuration.locale.language

    val defaultColorsForAvatar = listOf(
        0xffe57373, 0xfff06292, 0xffba68c8, 0xff9575cd, 0xff7986cb, 0xff64b5f6, 0xff4fc3f7,
        0xff4dd0e1, 0xff4db6ac, 0xff81c784, 0xffaed581, 0xffff8a65, 0xffd4e157, 0xffffd54f,
        0xffffb74d, 0xffa1887f, 0xff90a4ae
    )
}
