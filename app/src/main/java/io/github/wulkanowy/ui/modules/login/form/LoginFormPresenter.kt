package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.logRegister
import timber.log.Timber
import javax.inject.Inject

class LoginFormPresenter @Inject constructor(
    private val schedulers: SchedulersProvider,
    private val errorHandler: LoginErrorHandler,
    private val sessionRepository: SessionRepository
) : BasePresenter<LoginFormView>(errorHandler) {

    private var wasEmpty = false

    override fun onAttachView(view: LoginFormView) {
        super.onAttachView(view)
        view.initInputs()
    }

    fun attemptLogin(email: String, password: String, symbol: String, endpoint: String) {
        if (!validateCredentials(email, password, symbol)) return
        disposable.add(sessionRepository.getConnectedStudents(email, password, symbol, endpoint)
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.backgroundThread)
            .doOnSubscribe {
                view?.run {
                    hideSoftKeyboard()
                    showLoginProgress(true)
                    errorHandler.doOnBadCredentials = {
                        setErrorPassIncorrect()
                        showSoftKeyboard()
                        Timber.i("Entered wrong username or password")
                    }
                }
                sessionRepository.clearCache()
                Timber.i("Log endpoint: $endpoint")
            }
            .doFinally { view?.showLoginProgress(false) }
            .subscribe({
                view?.run {
                    if (it.isEmpty() && !wasEmpty) {
                        showSymbolInput()
                        wasEmpty = true
                    } else if (it.isEmpty() && wasEmpty) {
                        showSymbolInput()
                        setErrorSymbolIncorrect()
                        logRegister("No student found", it.size, false, "null", endpoint)
                    } else {
                        switchNextView()
                        logRegister("Found students", it.size,true, symbol, endpoint)
                    }
                }
            }, {
                errorHandler.proceed(it)
                logRegister(it.localizedMessage, 0, false, symbol, endpoint)
            }))
    }

    private fun validateCredentials(login: String, password: String, symbol: String): Boolean {
        var isCorrect = true

        if (login.isEmpty()) {
            view?.setErrorNicknameRequired()
            isCorrect = false
        }

        if (password.isEmpty()) {
            view?.setErrorPassRequired(focus = isCorrect)
            isCorrect = false
        }

        if (symbol.isEmpty() && wasEmpty) {
            view?.setErrorSymbolRequire()
            isCorrect = false
        }

        if (password.length < 6 && password.isNotEmpty()) {
            view?.setErrorPassInvalid(focus = isCorrect)
            isCorrect = false
        }
        return isCorrect
    }
}
