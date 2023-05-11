package io.github.wulkanowy.ui.modules.auth

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<AuthView>(errorHandler, studentRepository) {

    private var pesel: String = ""

    override fun onAttachView(view: AuthView) {
        super.onAttachView(view)
        view.enableAuthButton(pesel.length == 11)
        view.showSuccess(false)
        view.showProgress(false)
    }

    fun onPeselChange(newPesel: String?) {
        pesel = newPesel.orEmpty()

        view?.enableAuthButton(pesel.length == 11)
        view?.showPeselError(false)
    }

    fun authorize() {
        presenterScope.launch {
            view?.showProgress(true)

            runCatching { studentRepository.authorizePermission(pesel) }
                .onFailure { errorHandler.dispatch(it) }
                .onSuccess {
                    if (it) {
                        view?.showSuccess(true)
                        view?.showPeselError(false)
                    } else {
                        view?.showSuccess(false)
                        view?.showPeselError(true)
                    }
                }

            view?.showProgress(false)
        }
    }
}
