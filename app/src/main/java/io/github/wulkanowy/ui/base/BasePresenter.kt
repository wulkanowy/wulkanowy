package io.github.wulkanowy.ui.base

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

open class BasePresenter<T : BaseView>(
    protected val errorHandler: ErrorHandler,
    protected val studentRepository: StudentRepository,
    protected val schedulers: SchedulersProvider
) : CoroutineScope {

    var job: Job = Job()

    private val jobs = mutableMapOf<String, Job>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    @Deprecated("Use flow instead :)")
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
            flow {
                val student = studentRepository.getCurrentStudent(false)
                studentRepository.logoutStudent(student)

                val students = studentRepository.getSavedStudents(false)
                if (students.isNotEmpty()) {
                    Timber.i("Switching current student")
                    studentRepository.switchStudent(students[0])
                    emit(students[0])
                } else emit(null)
            }.catch {
                Timber.i("Switch student result: An exception occurred")
                errorHandler.dispatch(it)
            }.collect {
                Timber.i("Switch student result: Open login view")
                view?.openClearLoginView()
            }
        }
    }

    fun <T> Flow<T>.launch(individualJobTag: String = "load"): Job {
        jobs[individualJobTag]?.cancel()
        val job = launchIn(this@BasePresenter)
        jobs[individualJobTag] = job
        return job
    }

    fun cancelJobs(vararg names: String) {
        names.forEach {
            jobs[it]?.cancel()
        }
    }

    open fun onDetachView() {
        view = null
        disposable.clear()
        job.cancel()
        errorHandler.clear()
    }
}
