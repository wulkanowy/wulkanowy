package io.github.wulkanowy.data.repositories.recover

import io.github.wulkanowy.utils.flowWithResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoverRepository @Inject constructor(private val remote: RecoverRemote) {

    fun getReCaptchaSiteKey(host: String, symbol: String) = flowWithResource {
        remote.getReCaptchaSiteKey(host, symbol)
    }

    fun sendRecoverRequest(url: String, symbol: String, email: String, reCaptchaResponse: String) = flowWithResource {
        remote.sendRecoverRequest(url, symbol, email, reCaptchaResponse)
    }
}
