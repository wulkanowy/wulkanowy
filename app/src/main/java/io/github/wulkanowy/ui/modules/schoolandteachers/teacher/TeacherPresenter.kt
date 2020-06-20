package io.github.wulkanowy.ui.modules.schoolandteachers.teacher

import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.teacher.TeacherRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TeacherPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val teacherRepository: TeacherRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<TeacherView>(errorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    private var refreshJob: Job? = null

    private var loadingJob: Job? = null

    override fun onAttachView(view: TeacherView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Teacher view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        refreshData()
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        if (forceRefresh) refreshData()
        else loadData()
    }

    private fun refreshData() {
        refreshJob?.cancel()
        refreshJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emit(teacherRepository.refreshTeachers(student, semester))
            }.onEach { afterLoading() }.catch { handleError(it) }.collect()
        }
    }

    private fun loadData() {
        Timber.i("Loading teachers data started")

        loadingJob?.cancel()
        loadingJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emitAll(teacherRepository.getTeachers(student, semester))
            }.map {
                it.filter { item -> item.name.isNotBlank() }
            }.onEach {
                afterLoading()
            }.catch {
                handleError(it)
            }.collect {
                Timber.i("Loading teachers result: Success")
                view?.run {
                    updateData(it)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    showErrorView(false)
                }
                analytics.logEvent(
                    "load_data",
                    "type" to "teachers",
                    "items" to it.size
                )
            }
        }
    }

    private fun afterLoading() {
        view?.run {
            hideRefresh()
            showProgress(false)
            enableSwipe(true)
            notifyParentDataLoaded()
        }
    }

    private fun handleError(error: Throwable) {
        Timber.i("Loading teachers result: An exception occurred")
        errorHandler.dispatch(error)
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
            } else showError(message, error)
        }
    }
}
