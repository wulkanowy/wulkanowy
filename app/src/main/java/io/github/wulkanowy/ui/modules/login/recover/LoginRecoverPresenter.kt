package io.github.wulkanowy.ui.modules.login.recover

import io.github.wulkanowy.data.repositories.recover.RecoverRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LoginRecoverPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    studentRepository: StudentRepository,
    private val loginErrorHandler: RecoverErrorHandler,
    private val analytics: FirebaseAnalyticsHelper,
    private val recoverRepository: RecoverRepository
) : BasePresenter<LoginRecoverView>(loginErrorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    private var captchaConfirmJob: Job? = null

    override fun onAttachView(view: LoginRecoverView) {
        super.onAttachView(view)
        view.initView()

        with(loginErrorHandler) {
            showErrorMessage = ::showErrorMessage
            onInvalidUsername = ::onInvalidUsername
            onInvalidCaptcha = ::onInvalidCaptcha
        }
    }

    fun onNameTextChanged() {
        view?.clearUsernameError()
    }

    fun onHostSelected() {
        view?.run {
            if ("fakelog" in recoverHostValue) setDefaultCredentials("jan@fakelog.cf")
            clearUsernameError()
            updateFields()
        }
    }

    fun updateFields() {
        view?.run {
            setUsernameHint(if ("standard" in recoverHostValue) emailHintString else loginPeselEmailHintString)
        }
    }

    fun onRecoverClick() {
        val username = view?.recoverNameValue.orEmpty()
        val host = view?.recoverHostValue.orEmpty()
        val symbol = view?.formHostSymbol.orEmpty()

        if (!validateInput(username, host)) return

        launch {
            flow { emit(recoverRepository.getReCaptchaSiteKey(host, symbol.ifBlank { "Default" })) }
                .onStart {
                    view?.run {
                        hideSoftKeyboard()
                        showRecoverForm(false)
                        showProgress(true)
                        showErrorView(false)
                        showCaptcha(false)
                    }
                }.catch {
                    Timber.i("Obtain captcha site key result: An exception occurred")
                    errorHandler.dispatch(it)
                }.collect { (resetUrl, siteKey) ->
                    view?.loadReCaptcha(siteKey, resetUrl)
                }
        }
    }

    private fun validateInput(username: String, host: String): Boolean {
        var isCorrect = true

        if (username.isEmpty()) {
            view?.setErrorNameRequired()
            isCorrect = false
        }

        if ("standard" in host && "@" !in username) {
            view?.setUsernameError(view?.invalidEmailString.orEmpty())
            isCorrect = false
        }

        return isCorrect
    }

    fun onReCaptchaVerified(reCaptchaResponse: String) {
        val username = view?.recoverNameValue.orEmpty()
        val host = view?.recoverHostValue.orEmpty()
        val symbol = view?.formHostSymbol.ifNullOrBlank { "Default" }

        captchaConfirmJob?.cancel()
        captchaConfirmJob = launch {
            flow { emit(recoverRepository.sendRecoverRequest(host, symbol, username, reCaptchaResponse)) }
                .onStart {
                    view?.run {
                        showProgress(true)
                        showRecoverForm(false)
                        showCaptcha(false)
                    }
                }
                .onCompletion {
                    view?.showProgress(false)
                }.catch {
                    Timber.i("Send recover request result: An exception occurred")
                    errorHandler.dispatch(it)
                    analytics.logEvent("account_recover", "register" to host, "symbol" to symbol, "success" to false)
                }.collect {
                    view?.run {
                        showSuccessView(true)
                        setSuccessTitle(it.substringBefore(". "))
                        setSuccessMessage(it.substringAfter(". "))
                    }

                    analytics.logEvent("account_recover", "register" to host, "symbol" to symbol, "success" to true)
                }
        }
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun showErrorMessage(message: String, error: Throwable) {
        view?.run {
            lastError = error
            showProgress(false)
            setErrorDetails(message)
            showErrorView(true)
        }
    }

    private fun onInvalidUsername(message: String) {
        view?.run {
            setUsernameError(message)
            showRecoverForm(true)
        }
    }

    private fun onInvalidCaptcha(message: String, error: Throwable) {
        view?.run {
            lastError = error
            setErrorDetails(message)
            showCaptcha(false)
            showRecoverForm(false)
            showErrorView(true)
        }
    }
}
