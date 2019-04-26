package io.github.wulkanowy.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import timber.log.Timber

class DebugLogTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "Wulkanowy", message, t)
    }
}

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.setInt("priority", priority)
        Crashlytics.setString("tag", tag)

        if (t == null) Crashlytics.log(message)
        else Crashlytics.logException(t)
    }
}

class ActivityLifecycleLogger : Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} PAUSED") }
    }

    override fun onActivityResumed(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} RESUMED") }
    }

    override fun onActivityStarted(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} STARTED") }
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} DESTROYED") }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        activity?.let { Timber.d("${it::class.java.simpleName} SAVED INSTANCE STATE") }
    }

    override fun onActivityStopped(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} STOPPED") }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activity?.let { Timber.d("${it::class.java.simpleName} CREATED") }
    }
}
