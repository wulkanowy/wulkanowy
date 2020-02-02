package io.github.wulkanowy.ui.modules.login.recover

import io.github.wulkanowy.data.repositories.recover.RecoverRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.ifNullOrBlank
import timber.log.Timber
import javax.inject.Inject

class LoginRecoverPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    studentRepository: StudentRepository,
    private val loginErrorHandler: RecoverErrorHandler,
    private val analytics: FirebaseAnalyticsHelper,
    private val recoverRepository: RecoverRepository
) : BasePresenter<LoginRecoverView>(loginErrorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LoginRecoverView) {
        super.onAttachView(view)
        view.initView()
        loginErrorHandler.onInvalidUsername = { view.setUsernameError(it) }
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

    fun onConfirmClick() {
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
                    showProgress(true)
                    showRecoverForm(false)
                    showCaptcha(false)
                }
            }
            .subscribe({ (resetUrl, siteKey) ->
                Timber.d(siteKey)
                view?.loadReCaptcha(siteKey, resetUrl)
            }) {
                errorHandler.dispatch(it)
            })
    }

    fun sendRecoverRequest(reCaptchaResponse: String) {
        val username = view?.recoverNameValue.orEmpty()
        val host = view?.recoverHostValue.orEmpty()
        val symbol = view?.recoverSymbolValue.ifNullOrBlank { "Default" }

        with(disposable) {
            clear()
            add(recoverRepository.sendRecoverRequest(host, symbol, username, reCaptchaResponse)
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doOnSubscribe {
                    view?.apply {
                        showProgress(true)
                        showRecoverForm(false)
                        showCaptcha(false)
                    }
                }
                .doFinally {
                    view?.apply {
                        showProgress(false)
                        showRecoverForm(true)
                    }
                }
                .subscribe({
                    view?.showMessage("Wysłano wiadomość na podany email.")
                }) {
                    errorHandler.dispatch(it)
                })
        }
    }
}
