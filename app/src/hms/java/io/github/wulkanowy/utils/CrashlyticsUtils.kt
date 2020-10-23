package io.github.wulkanowy.utils

import android.util.Log
import com.huawei.agconnect.crash.AGConnectCrash
import fr.bipi.tressence.base.FormatterPriorityTree
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CrashlyticsTree : FormatterPriorityTree(Log.VERBOSE) {

    private val crashlytics by lazy { AGConnectCrash.getInstance() }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (skipLog(priority, tag, message, t)) return

        crashlytics.log(format(priority, tag, message))
    }
}

class CrashlyticsExceptionTree : FormatterPriorityTree(Log.ERROR) {

    private val crashlytics by lazy { AGConnectCrash.getInstance() }

    override fun skipLog(priority: Int, tag: String?, message: String, t: Throwable?): Boolean {
        if (t is FeatureDisabledException || t is FeatureNotAvailableException || t is UnknownHostException || t is SocketTimeoutException || t is InterruptedIOException) {
            return true
        }

        return super.skipLog(priority, tag, message, t)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (skipLog(priority, tag, message, t)) return

        crashlytics.setCustomKey("priority", priority)
        crashlytics.setCustomKey("tag", tag.orEmpty())
        crashlytics.setCustomKey("message", message)
        if (t != null) {
            //crashlytics.log(t)
        } else {
            //crashlytics.log(StackTraceRecorder(format(priority, tag, message)))
        }
    }
}
