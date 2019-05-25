package io.github.wulkanowy.utils.logger

import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // do nothing
    }
}
