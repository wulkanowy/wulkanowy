package io.github.wulkanowy.ui.modules.login.symbol

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import java.io.Serializable
import javax.inject.Inject

class LoginSymbolPresenter @Inject constructor(
    private val studentRepository: StudentRepository,
    private val errorHandler: LoginErrorHandler,
    private val schedulers: SchedulersProvider
) : BasePresenter<LoginSymbolView>(errorHandler) {

    lateinit var loginData: Triple<String, String, String>

    fun onAttachView(view: LoginSymbolView, savedLoginData: Serializable?) {
        super.onAttachView(view)
        view.initView()
    }

    fun attemptLogin(symbol: String) {
        if (symbol.isBlank()) {
            view?.setErrorSymbolRequire()
            return
        }

        disposable.add(studentRepository.getStudents(loginData.first, loginData.second, loginData.third, symbol)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.apply {
                    hideSoftKeyboard()
                    showProgress(true)
                    showContent(false)
                }
            }
            .doFinally {
                view?.apply {
                    showProgress(false)
                    showContent(true)
                }
            }
            .subscribe({
                if (it.isEmpty()) {
                    view?.setErrorSymbolIncorrect()
                } else view?.notifyParentAccountLogged(it)
            }, { errorHandler.dispatch(it) }))
    }

    fun onParentInitSymbolView(loginData: Triple<String, String, String>) {
        this.loginData = loginData
        view?.apply {
            clearAndFocusSymbol()
            showSoftKeyboard()
        }
    }
}
