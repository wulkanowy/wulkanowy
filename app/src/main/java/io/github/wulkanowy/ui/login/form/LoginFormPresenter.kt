package io.github.wulkanowy.ui.login.form

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginFormPresenter @Inject constructor(disposable: CompositeDisposable,
                                             private val schedulers: SchedulersManager,
                                             private val studentRepository: StudentRepository)
    : BasePresenter<LoginFormContract.View>(disposable), LoginFormContract.Presenter {

    override fun attemptLogin(email: String, password: String) {
        if (!validateCredentials(email, password)) return

        ReactiveNetwork.checkInternetConnectivity()
                .flatMap { isConnected ->
                    if (isConnected) {
                        return@flatMap studentRepository.getConnectedStudents(email, password)
                    }
                    Single.error<List<Student>>(RuntimeException())
                }
                .observeOn(schedulers.mainThread())
                .subscribeOn(schedulers.backgroundThread())
                .subscribe({ students -> }, { exception -> view?.showNoNetworkMessage() })

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