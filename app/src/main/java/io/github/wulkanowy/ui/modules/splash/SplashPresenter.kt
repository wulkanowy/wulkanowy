package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    private val studentRepository: StudentRepository,
    private val errorHandler: ErrorHandler,
    private val preferencesRepository: PreferencesRepository,
    private val schedulers: SchedulersProvider
) : BasePresenter<SplashView>(errorHandler) {

    override fun onAttachView(view: SplashView) {
        super.onAttachView(view)
        disposable.add(studentRepository.isStudentSaved()
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                view.apply {
                    if (it) openMainView(preferencesRepository.isAMOLEDMode)
                    else openLoginView()
                }
            }, { errorHandler.dispatch(it) }))
    }
}
