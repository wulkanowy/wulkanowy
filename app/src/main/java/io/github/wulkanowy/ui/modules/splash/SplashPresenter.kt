package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<SplashView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: SplashView) {
        super.onAttachView(view)
        launch {
            flow { emit(studentRepository.isCurrentStudentSet()) }
                .catch { errorHandler.dispatch(it) }
                .collect {
                    view.apply {
                        if (it) openMainView()
                        else openLoginView()
                    }
                }
        }
    }
}
