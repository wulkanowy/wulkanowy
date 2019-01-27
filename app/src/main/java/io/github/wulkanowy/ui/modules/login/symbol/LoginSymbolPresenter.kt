package io.github.wulkanowy.ui.modules.login.symbol

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Single
import java.io.Serializable
import javax.inject.Inject

class LoginSymbolPresenter @Inject constructor(
    private val studentRepository: StudentRepository,
    private val errorHandler: LoginErrorHandler,
    private val schedulers: SchedulersProvider
) : BasePresenter<LoginSymbolView>(errorHandler) {

    var loginData: Triple<String, String, String>? = null

    @Suppress("UNCHECKED_CAST")
    fun onAttachView(view: LoginSymbolView, savedLoginData: Serializable?) {
        super.onAttachView(view)
        view.initView()
        if (savedLoginData is Triple<*, *, *>) {
            loginData = savedLoginData as Triple<String, String, String>
        }
    }

    fun attemptLogin(symbol: String) {
        if (symbol.isBlank()) {
            view?.setErrorSymbolRequire()
            return
        }

        disposable.add(
            Single.fromCallable { if (loginData == null) throw IllegalArgumentException("Login data is null") else loginData }
                .flatMap { studentRepository.getStudents(it.first, it.second, it.third, symbol) }
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
                    view?.apply {
                        if (it.isEmpty()) setErrorSymbolIncorrect()
                        else notifyParentAccountLogged(it)
                    }
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
