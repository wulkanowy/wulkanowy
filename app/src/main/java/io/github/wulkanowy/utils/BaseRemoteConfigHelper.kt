package io.github.wulkanowy.utils

abstract class BaseRemoteConfigHelper {

    open fun fetchAndActivate(callback: (RemoteConfigHelper) -> Unit) = Unit

    open val userAgentTemplate: String = RemoteConfigDefaults.USER_AGENT_TEMPLATE.value as String
}
