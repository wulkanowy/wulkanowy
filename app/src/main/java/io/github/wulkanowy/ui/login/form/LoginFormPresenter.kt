package io.github.wulkanowy.ui.login.form

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.login.LoginErrorHandler
import io.github.wulkanowy.utils.DEFAULT_SYMBOL
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import javax.inject.Inject

class LoginFormPresenter @Inject constructor(private val schedulers: SchedulersManager,
                                             private val errorHandler: LoginErrorHandler,
                                             private val studentRepository: StudentRepository)
    : BasePresenter<LoginFormView>(errorHandler) {

    private var wasEmpty = false

    fun attemptLogin(email: String, password: String, symbol: String) {
        if (!validateCredentials(email, password, symbol)) return
        disposable.add(studentRepository.getConnectedStudents(email, password, normalizeSymbol(symbol))
                .observeOn(schedulers.mainThread())
                .subscribeOn(schedulers.backgroundThread())
                .doOnSubscribe {
                    view?.run {
                        hideSoftKeyboard()
                        showLoginProgress(true)
                    }
                    errorHandler.doOnBadCredentials = {
                        view?.run {
                            setErrorPassIncorrect()
                            showSoftKeyboard()
                        }
                    }
                    studentRepository.clearCache()
                }
                .doFinally { view?.showLoginProgress(false) }
                .subscribe({
                    if (it.isEmpty() && !wasEmpty) {
                        showSymbolForm()
                        wasEmpty = true
                    } else if (it.isEmpty() && wasEmpty) {
                        showSymbolForm()
                        view?.setErrorSymbolIncorrect()
                    } else {
                        view?.switchNextView()
                    }
                }, { errorHandler.proceed(it) }))
    }

    private fun validateCredentials(email: String, password: String, symbol: String): Boolean {
        var isCorrect = true

        if (email.isEmpty()) {
            view?.setErrorEmailRequired()
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

        if (!email.contains("[@]|[/]{4}".toRegex())) {
            view?.setErrorEmailInvalid()
            isCorrect = false
        }

        if (password.length <= 4) {
            view?.setErrorPassInvalid(focus = isCorrect)
            isCorrect = false
        }
        return isCorrect
    }

    private fun normalizeSymbol(symbol: String): String {
        return if (symbol.isEmpty()) DEFAULT_SYMBOL else symbol
    }

    private fun showSymbolForm() {
        view?.run {
            showSymbolInput()
            showSoftKeyboard()
        }
    }
}
