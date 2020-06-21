package io.github.wulkanowy.ui.modules.login.symbol

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

class LoginSymbolPresenter @Inject constructor(
    studentRepository: StudentRepository,
    schedulers: SchedulersProvider,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LoginSymbolView>(loginErrorHandler, studentRepository, schedulers) {

    private var lastError: Throwable? = null

    var loginData: Triple<String, String, String>? = null

    @Suppress("UNCHECKED_CAST")
    fun onAttachView(view: LoginSymbolView, savedLoginData: Serializable?) {
        super.onAttachView(view)
        view.run {
            initView()
            showContact(false)
        }
        if (savedLoginData is Triple<*, *, *>) {
            loginData = savedLoginData as Triple<String, String, String>
        }
    }

    fun onSymbolTextChanged() {
        view?.apply { if (symbolNameError != null) clearSymbolError() }
    }

    fun attemptLogin(symbol: String) {
        if (loginData == null) throw IllegalArgumentException("Login data is null")

        if (symbol.isBlank()) {
            view?.setErrorSymbolRequire()
            return
        }

        launch {
            flow { emit(studentRepository.getStudentsScrapper(loginData!!.first, loginData!!.second, loginData!!.third, symbol)) }.onStart {
                view?.apply {
                    hideSoftKeyboard()
                    showProgress(true)
                    showContent(false)
                }
                Timber.i("Login with symbol started")
            }.onCompletion {
                view?.apply {
                    showProgress(false)
                    showContent(true)
                }
            }.catch {
                Timber.i("Login with symbol result: An exception occurred")
                analytics.logEvent("registration_symbol", "success" to false, "students" to -1, "scrapperBaseUrl" to loginData?.third, "symbol" to symbol, "error" to it.message.ifNullOrBlank { "No message" })
                loginErrorHandler.dispatch(it)
                lastError = it
                view?.showContact(true)
            }.collect {
                analytics.logEvent("registration_symbol", "success" to true, "students" to it.size, "scrapperBaseUrl" to loginData?.third, "symbol" to symbol, "error" to "No error")
                view?.apply {
                    if (it.isEmpty()) {
                        Timber.i("Login with symbol result: Empty student list")
                        setErrorSymbolIncorrect()
                        view?.showContact(true)
                    } else {
                        Timber.i("Login with symbol result: Success")
                        notifyParentAccountLogged(it)
                    }
                }
            }
        }
    }

    fun onParentInitSymbolView(loginData: Triple<String, String, String>) {
        this.loginData = loginData
        view?.apply {
            clearAndFocusSymbol()
            showSoftKeyboard()
        }
    }

    fun onFaqClick() {
        view?.openFaqPage()
    }

    fun onEmailClick() {
        view?.openEmail(loginData?.third.orEmpty(), lastError?.message.ifNullOrBlank { "empty" })
    }
}
