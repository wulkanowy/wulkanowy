package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class LoginPresenter @Inject constructor(errorHandler: ErrorHandler) : BasePresenter<LoginView>(errorHandler) {

    override fun onAttachView(view: LoginView) {
        super.onAttachView(view)
        view.run {
            initAdapter()
            hideActionBar()
        }
        Timber.i("Login view is attached")
    }

    fun onFormViewAccountLogged(students: List<Student>, email: String, pass: String, endpoint: String) {
        view?.apply {
            if (!students.isEmpty()) {
                notifyInitSymbolFragment(email, pass, endpoint)
                switchView(1)
            } else {
                notifyInitStudentSelectFragment(students)
                switchView(2)
            }
        }
    }

    fun onSymbolViewAccountLogged(students: List<Student>) {
        view?.apply {
            notifyInitStudentSelectFragment(students)
            switchView(2)
        }
    }

    fun onBackPressed(default: () -> Unit) {
        Timber.i("Back pressed in login view")
        view?.apply {
            when (currentViewIndex) {
                2 -> {
                    switchView(0)
                    hideActionBar()
                }
                1 -> switchView(0)
                else -> default()
            }
        }
    }
}
