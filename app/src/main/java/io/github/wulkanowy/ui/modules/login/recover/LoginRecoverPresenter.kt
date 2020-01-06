package io.github.wulkanowy.ui.modules.login.recover

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.data.repositories.recover.RecoverRepository
import io.github.wulkanowy.utils.ifNullOrBlank
import timber.log.Timber
import javax.inject.Inject

class LoginRecoverPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: FirebaseAnalyticsHelper,
    private val recoverRepository: RecoverRepository
) : BasePresenter<LoginRecoverView>(loginErrorHandler, studentRepository, schedulers) {
    override fun onAttachView(view: LoginRecoverView) {
        super.onAttachView(view)
        view.run {
            initView()
        }
    }

    fun onNameTextChanged() {
        view?.clearNameError()
    }

    fun onSymbolTextChanged() {
        view?.clearSymbolError()
    }

    fun onConfirmClick() {
        if (view?.recoverNameValue.orEmpty().isEmpty()) {
            view?.setErrorNameRequired()
            return
        }
        disposable.add(recoverRepository.getRecaptchaSitekey(view?.recoverHostValue.orEmpty(), view?.recoverSymbolValue.ifNullOrBlank { "Default" })
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.apply {
                    hideSoftKeyboard()
                    showProgress(true)
                    showContentForm(false)
                    showContentCaptcha(false)
                }
            }
            .subscribe({
                Timber.d(it.second)
                view?.loadRecaptcha(it.second, it.first)
            }) {
                errorHandler.dispatch(it)
            })
    }

    fun onHostSelected() {

        view?.apply {
            clearNameError()
            if (recoverHostValue?.contains("fakelog") == true) {
                setDefaultCredentials("jan@fakelog.cf", "Default")
            }
        }
    }

    fun sendRecoverRequest(recaptchaResponse: String) {
        disposable.clear()
        disposable.add(recoverRepository.sendRecoverRequest(view?.recoverHostValue.orEmpty(), view?.recoverSymbolValue.ifNullOrBlank { "Default" }, view?.recoverNameValue.orEmpty(), recaptchaResponse)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.apply {
                    showProgress(true)
                    showContentForm(false)
                    showContentCaptcha(false)
                }
            }
            .doFinally{
                view?.apply {
                    showProgress(false)
                    showContentForm(true)
                }
            }
            .subscribe({
                view?.showMessage("Wysłano wiadomość na podany email.")
            }) {
                errorHandler.dispatch(it)
            })
    }
}