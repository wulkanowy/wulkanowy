package io.github.wulkanowy.ui.login.options

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import javax.inject.Inject

class LoginOptionsPresenter @Inject constructor(private val errorHandler: ErrorHandler,
                                                private val repository: StudentRepository,
                                                private val schedulers: SchedulersManager)
    : BasePresenter<LoginOptionsView>(errorHandler) {

    fun refreshData() {
        disposable.add(repository.cachedStudents
                .observeOn(schedulers.mainThread())
                .subscribeOn(schedulers.backgroundThread())
                .subscribe({
                    view?.updateData(it.map { student ->
                        LoginOptionsItem(student)
                    })
                }, { errorHandler.proceed(it) }))
    }
}