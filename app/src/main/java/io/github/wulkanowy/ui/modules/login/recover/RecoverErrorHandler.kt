package io.github.wulkanowy.ui.modules.login.recover

import android.content.res.Resources
import com.readystatesoftware.chuck.api.ChuckCollector
import io.github.wulkanowy.sdk.scrapper.exception.InvalidEmailException
import io.github.wulkanowy.sdk.scrapper.exception.NoAccountFoundException
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class RecoverErrorHandler @Inject constructor(
    resources: Resources,
    chuckCollector: ChuckCollector
) : ErrorHandler(resources, chuckCollector) {

    var onInvalidUsername: (String) -> Unit = {}

    override fun proceed(error: Throwable) {
        when (error) {
            is InvalidEmailException, is NoAccountFoundException -> onInvalidUsername(error.localizedMessage.orEmpty())
            else -> super.proceed(error)
        }
    }

    override fun clear() {
        super.clear()
        onInvalidUsername = {}
    }
}
