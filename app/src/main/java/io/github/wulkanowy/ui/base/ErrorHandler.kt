package io.github.wulkanowy.ui.base

import android.content.res.Resources
import com.chuckerteam.chucker.api.ChuckerCollector
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.sdk.exception.BadCredentialsException
import io.github.wulkanowy.sdk.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.exception.NotLoggedInException
import io.github.wulkanowy.sdk.exception.ServiceUnavailableException
import io.github.wulkanowy.utils.getString
import io.github.wulkanowy.utils.security.ScramblerException
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

open class ErrorHandler @Inject constructor(protected val resources: Resources, private val chuckerCollector: ChuckerCollector) {

    var showErrorMessage: (String, Throwable) -> Unit = { _, _ -> }

    var onSessionExpired: () -> Unit = {}

    var onNoCurrentStudent: () -> Unit = {}

    fun dispatch(error: Throwable) {
        chuckerCollector.onError(error.javaClass.simpleName, error)
        Timber.e(error, "An exception occurred while the Wulkanowy was running")
        proceed(error)
    }

    protected open fun proceed(error: Throwable) {
        val message = resources.getString(error)
        when (error) {
            is UnknownHostException -> showErrorMessage(message, error)
            is SocketTimeoutException -> showErrorMessage(message, error)
            is NotLoggedInException -> showErrorMessage(message, error)
            is ServiceUnavailableException -> showErrorMessage(message, error)
            is FeatureDisabledException -> showErrorMessage(message, error)
            is ScramblerException, is BadCredentialsException -> onSessionExpired()
            is NoCurrentStudentException -> onNoCurrentStudent()
            is FeatureNotAvailableException -> showErrorMessage(message, error)
            else -> showErrorMessage(message, error)
        }
    }

    open fun clear() {
        showErrorMessage = { _, _ -> }
        onSessionExpired = {}
        onNoCurrentStudent = {}
    }
}
