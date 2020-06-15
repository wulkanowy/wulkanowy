package io.github.wulkanowy.ui.base

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

open class BasePresenter<T : BaseView>(
    protected val errorHandler: ErrorHandler,
    protected val studentRepository: StudentRepository,
    protected val schedulers: SchedulersProvider
) : CoroutineScope {

    private val job = Job()

    override val coroutineContext = job + Dispatchers.IO

    val disposable = CompositeDisposable()

    var view: T? = null

    open fun onAttachView(view: T) {
        this.view = view
        errorHandler.apply {
            showErrorMessage = view::showError
            onSessionExpired = view::showExpiredDialog
            onNoCurrentStudent = view::openClearLoginView
        }
    }

    fun onExpiredLoginSelected() {
        Timber.i("Attempt to switch the student after the session expires")
        launch {
            val student = studentRepository.getCurrentStudent(false)
            studentRepository.logoutStudent(student)
            val students = studentRepository.getSavedStudents(false)
            if (students.isNotEmpty()) {
                Timber.i("Switching current student")
                studentRepository.switchStudent(students[0])
            }

            withContext(Dispatchers.Main) {
                Timber.i("Switch student result: Open login view")
                view?.openClearLoginView()
            }

//            Timber.i("Switch student result: An exception occurred")
//            errorHandler.dispatch(it)
        }
    }

    open fun onDetachView() {
        view = null
        disposable.clear()
        job.cancel()
        errorHandler.clear()
    }
}
