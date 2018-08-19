package io.github.wulkanowy.ui.splash

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SplashPresenter @Inject constructor(private val studentRepository: StudentRepository,
                                          disposable: CompositeDisposable)
    : BasePresenter<SplashView>(disposable) {

    override fun attachView(view: SplashView) {
        super.attachView(view)
        view.cancelNotifications()

        if (studentRepository.isStudentLoggedIn) {
            view.openMainActivity()
        } else {
            view.openLoginActivity()
        }
    }
}
