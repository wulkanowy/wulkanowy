package io.github.wulkanowy.data.repositories.recover

import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoverRepository @Inject constructor(private val remote: RecoverRemote) {

    fun getRecaptchaSitekey(host: String, symbol: String) : Single<Pair<String, String>>{
        return remote.getRecaptchaSitekey(host, symbol)
    }

    fun sendRecoverRequest(url: String, symbol: String, email:String, recaptchaResponse: String) : Single<String> {
        return remote.sendRecoverRequest(url, symbol, email, recaptchaResponse)
    }
}
