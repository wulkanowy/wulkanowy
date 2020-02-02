package io.github.wulkanowy.data.repositories.recover

import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoverRepository @Inject constructor(private val remote: RecoverRemote) {

    fun getReCaptchaSiteKey(host: String, symbol: String): Single<Pair<String, String>> {
        return remote.getReCaptchaSiteKey(host, symbol)
    }

    fun sendRecoverRequest(url: String, symbol: String, email: String, reCaptchaResponse: String): Single<String> {
        return remote.sendRecoverRequest(url, symbol, email, reCaptchaResponse)
    }
}
