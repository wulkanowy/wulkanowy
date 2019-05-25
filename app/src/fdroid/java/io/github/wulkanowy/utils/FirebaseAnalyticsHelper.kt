package io.github.wulkanowy.utils

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper @Inject constructor() {

    fun logEvent(name: String, vararg params: Pair<String, Any?>) {
        Timber.d("Logging event (FE): $name, Bundle[{${params.joinToString(", ") {
            it.first + "=" + it.second
        }}}]")
    }
}
