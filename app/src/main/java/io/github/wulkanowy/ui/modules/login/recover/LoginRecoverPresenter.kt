package io.github.wulkanowy.ui.modules.login.recover

import io.github.wulkanowy.data.repositories.recover.RecoverRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.ifNullOrBlank
import timber.log.Timber
import javax.inject.Inject

class LoginRecoverPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    studentRepository: StudentRepository,
    private val loginErrorHandler: RecoverErrorHandler,
    private val analytics: FirebaseAnalyticsHelper, // TODO
    private val recoverRepository: RecoverRepository
) : BasePresenter<LoginRecoverView>(loginErrorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LoginRecoverView) {
        super.onAttachView(view)
        view.initView()
        loginErrorHandler.onInvalidUsername = {
            with(view) {
                showRecoverForm(true)
                setUsernameError(it)
            }
        }
        loginErrorHandler.onInvalidCaptcha = {
            with(view) {
                setErrorMessage(it)
                showCaptcha(false)
                showRecoverForm(false)
                showErrorView(true)
            }
        }
    }

    fun onNameTextChanged() {
        view?.clearUsernameError()
    }

    fun onSymbolTextChanged() {
        view?.clearSymbolError()
    }

    fun onHostSelected() {
        view?.run {
            if ("fakelog" in recoverHostValue) setDefaultCredentials("jan@fakelog.cf", "Default")
            clearUsernameError()
        }
    }

    fun onRecoverClick() {
        val username = view?.recoverNameValue.orEmpty()
        val host = view?.recoverHostValue.orEmpty()
        val symbol = view?.recoverSymbolValue.ifNullOrBlank { "Default" }

        if (username.isEmpty()) {
            view?.setErrorNameRequired()
            return
        }

        disposable.add(recoverRepository.getReCaptchaSiteKey(host, symbol)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.run {
                    hideSoftKeyboard()
                    showRecoverForm(false)
                    showProgress(true)
                    showErrorView(false)
                    showCaptcha(false)
                }
            }
            .subscribe({ (resetUrl, siteKey) ->
                view?.loadReCaptcha(siteKey, resetUrl)
            }) {
                Timber.e("Obtain captcha site key result: An exception occurred")
                errorHandler.dispatch(it)
            })
    }

    fun onReCaptchaVerified(reCaptchaResponse: String) {
        val username = view?.recoverNameValue.orEmpty()
        val host = view?.recoverHostValue.orEmpty()
        val symbol = view?.recoverSymbolValue.ifNullOrBlank { "Default" }

        with(disposable) {
            clear()
            add(recoverRepository.sendRecoverRequest(host, symbol, username, reCaptchaResponse)
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doOnSubscribe {
                    view?.run {
                        showProgress(true)
                        showRecoverForm(false)
                        showCaptcha(false)
                    }
                }
                .doFinally {
                    view?.showProgress(false)
                }
                .subscribe({
                    view?.run {
                        showSuccessView(true)
                        setSuccessMessage(it)
                    }
                }) {
                    Timber.e("Send recover request result: An exception occurred")
                    errorHandler.dispatch(it)
                })
        }
    }
}
