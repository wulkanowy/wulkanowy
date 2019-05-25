package io.github.wulkanowy.utils.logger

import com.crashlytics.android.Crashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.setInt("priority", priority)
        Crashlytics.setString("tag", tag)

        if (t == null) Crashlytics.log(message)
        else Crashlytics.logException(t)
    }
}
