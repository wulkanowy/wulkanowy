package io.github.wulkanowy.utils

import android.util.Log
import fr.bipi.tressence.crash.CrashlyticsLogExceptionTree
import fr.bipi.tressence.crash.CrashlyticsLogTree
import io.github.wulkanowy.sdk.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import java.net.UnknownHostException

class CrashlyticsTree : CrashlyticsLogTree(Log.VERBOSE) {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t is FeatureDisabledException || t is FeatureNotAvailableException || t is UnknownHostException) return

        super.log(priority, tag, message, t)
    }
}

class CrashlyticsExceptionTree : CrashlyticsLogExceptionTree()
