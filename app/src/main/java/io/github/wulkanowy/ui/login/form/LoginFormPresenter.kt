package io.github.wulkanowy.ui.login.form

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginFormPresenter @Inject constructor(private val disposable: CompositeDisposable,
                                             private val schedulers: SchedulersManager,
                                             private val studentRepository: StudentRepository)
    : BasePresenter<LoginFormView>(disposable) {

    fun attemptLogin(email: String, password: String) {
        if (!validateCredentials(email, password)) return

        view?.run {
            hideSoftKeyboard()
            showLoginProgress(true)
        }

        disposable.add(studentRepository.getConnectedStudents(email, password)
                .observeOn(schedulers.mainThread())
                .subscribeOn(schedulers.backgroundThread())
                .subscribe({ students ->
                    view?.run {
                        showLoginProgress(false)
                    }
                }, { exception -> view?.showNoNetworkMessage() }))

    }

    private fun validateCredentials(email: String, password: String): Boolean {
        var isCorrect = true

        if (email.isEmpty()) {
            view?.setErrorEmailRequired()
            isCorrect = false
        }

        if (password.isEmpty()) {
            view?.setErrorPassRequired(focus = isCorrect)
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
}