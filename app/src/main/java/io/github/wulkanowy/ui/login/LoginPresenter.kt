package io.github.wulkanowy.ui.login

import android.text.TextUtils
import io.github.wulkanowy.data.repositories.ResourceRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.AppConstant
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginPresenter @Inject constructor(disposable: CompositeDisposable,
                                         private val schedulers: SchedulersManager,
                                         private val resourceRepository: ResourceRepository,
                                         private val studentRepository: StudentRepository)
    : BasePresenter<LoginContract.View>(disposable), LoginContract.Presenter {

    private var email: String? = null

    private var password: String? = null

    private var symbol: String? = null

    override fun attemptLogin(email: String, password: String) {
        view?.resetViewErrors()

        this.email = email
        this.password = password

        if (!isAllFieldCorrect(password, email)) {
            view?.showSoftKeyboard()
            return
        }

        view?.showLoginProgress(true)

        studentRepository.getConnectedStudents(email, password)
                .observeOn(schedulers.mainThread())
                .subscribeOn(schedulers.backgroundThread())
                .subscribe { _ -> view?.showLoginProgress(false) }

        view?.hideSoftKeyboard()
    }


    private fun isEmailValid(email: String): Boolean {
        return email.contains("@") || email.contains("\\\\")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    private fun getNormalizedSymbol(symbol: String): String? {
        if (TextUtils.isEmpty(symbol)) {
            return AppConstant.DEFAULT_SYMBOL
        }

        val keys: Array<String> = resourceRepository.getSymbolsKeysArray()
        val values: Array<String> = resourceRepository.getSymbolsValuesArray()
        val map = LinkedHashMap<String, String>()

        for (i in 0 until Math.min(keys.size, values.size)) {
            map[keys[i]] = values[i]
        }

        return if (map.containsKey(symbol)) {
            map[symbol]
        } else AppConstant.DEFAULT_SYMBOL
    }

    private fun isAllFieldCorrect(password: String, email: String): Boolean {
        var correct = true

        if (TextUtils.isEmpty(password)) {
            view!!.setErrorPassRequired()
            correct = false
        } else if (!isPasswordValid(password)) {
            view!!.setErrorPassInvalid()
            correct = false
        }

        if (TextUtils.isEmpty(email)) {
            view!!.setErrorEmailRequired()
            correct = false
        } else if (!isEmailValid(email)) {
            view!!.setErrorEmailInvalid()
            correct = false
        }
        return correct
    }
}
