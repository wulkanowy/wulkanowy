package io.github.wulkanowy.data.repositories.recover

import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoverRemote @Inject constructor(private val sdk: Sdk) {
    fun getRecaptchaSitekey(host: String, symbol: String): Single<Pair<String, String>> {
        return sdk.getPasswordResetCaptchaCode(host, symbol)
    }

    fun sendRecoverRequest(url: String, symbol: String, email:String, recaptchaResponse: String) : Single<Pair<Boolean,String>>{
        return sdk.sendPasswordResetRequest(url, symbol, email, recaptchaResponse)
    }
}

