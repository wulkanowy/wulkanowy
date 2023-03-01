package io.github.wulkanowy.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNUSED_PARAMETER")
class RemoteConfigHelper @Inject constructor() : BaseRemoteConfigHelper() {

    override fun fetchAndActivate(callback: (RemoteConfigHelper) -> Unit) {
        callback(this)
    }

    override val userAgentTemplate: String
        get() = RemoteConfigDefaults.USER_AGENT_TEMPLATE.value as String
}
