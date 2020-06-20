package io.github.wulkanowy.ui.modules.schoolandteachers.school

import io.github.wulkanowy.data.repositories.school.SchoolRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SchoolPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schoolRepository: SchoolRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<SchoolView>(errorHandler, studentRepository, schedulers) {

    private var address: String? = null

    private var contact: String? = null

    private lateinit var lastError: Throwable

    private var refreshJob: Job? = null

    private var loadingJob: Job? = null

    override fun onAttachView(view: SchoolView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("School view was initialized")
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
        refreshData()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        if (forceRefresh) refreshData()
        else loadData()
    }

    fun onAddressSelected() {
        address?.let { view?.openMapsLocation(it) }
    }

    fun onTelephoneSelected() {
        contact?.let { view?.dialPhone(it) }
    }

    private fun refreshData() {
        refreshJob?.cancel()
        refreshJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emit(schoolRepository.refreshSchool(student, semester))
            }.onEach { afterLoading() }.catch { handleError(it) }.collect()
        }
    }

    private fun loadData() {
        Timber.i("Loading school info started")

        loadingJob?.cancel()
        loadingJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emitAll(schoolRepository.getSchoolInfo(student, semester))
            }.onEach {
                afterLoading()
            }.catch {
                handleError(it)
            }.collect {
                if (it != null) {
                    Timber.i("Loading teachers result: Success")
                    view?.run {
                        address = it.address.ifBlank { null }
                        contact = it.contact.ifBlank { null }
                        updateData(it)
                        showContent(true)
                        showEmpty(false)
                        showErrorView(false)
                    }
                    analytics.logEvent(
                        "load_item",
                        "type" to "school"
                    )
                } else {
                    Timber.i("Loading school result: No school info found")
                    view?.run {
                        showContent(!isViewEmpty)
                        showEmpty(isViewEmpty)
                        showErrorView(false)
                    }
                }
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
        Timber.i("Loading school result: An exception occurred")
        errorHandler.dispatch(error)
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
                showContent(false)
            } else showError(message, error)
        }
    }
}
