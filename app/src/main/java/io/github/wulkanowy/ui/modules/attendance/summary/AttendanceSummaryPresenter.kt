package io.github.wulkanowy.ui.modules.attendance.summary

import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.repositories.attendancesummary.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.subject.SubjectRepository
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
import org.threeten.bp.Month
import timber.log.Timber
import javax.inject.Inject

class AttendanceSummaryPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val subjectRepository: SubjectRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<AttendanceSummaryView>(errorHandler, studentRepository, schedulers) {

    private var subjects = emptyList<Subject>()

    var currentSubjectId = -1
        private set

    private lateinit var lastError: Throwable

    private var refreshJob: Job? = null

    private var loadJob: Job? = null

    fun onAttachView(view: AttendanceSummaryView, subjectId: Int?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Attendance summary view was initialized with subject id ${subjectId ?: -1}")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData(subjectId ?: -1)
        loadSubjects()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the attendance summary")
        refreshData(currentSubjectId)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        refreshData(currentSubjectId)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onSubjectSelected(name: String?) {
        Timber.i("Select attendance summary subject $name")
        view?.run {
            showContent(false)
            showProgress(true)
            enableSwipe(false)
            showEmpty(false)
            showErrorView(false)
            clearView()
        }
        (subjects.singleOrNull { it.name == name }?.realId ?: -1).let {
            if (it != currentSubjectId) loadData(it)
        }
    }

    private fun refreshData(subjectId: Int) {
        refreshJob?.cancel()
        refreshJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emit(attendanceSummaryRepository.refreshAttendanceSummary(student, semester, subjectId))
            }.onEach { afterLoading() }.catch { handleError(it) }.collect()
        }
    }

    private fun loadData(subjectId: Int) {
        Timber.i("Loading attendance summary data started")
        currentSubjectId = subjectId

        loadJob?.cancel()
        loadJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emitAll(attendanceSummaryRepository.getAttendanceSummary(student, semester, subjectId))
            }.map { items ->
                items.sortedByDescending { if (it.month.value <= Month.JUNE.value) it.month.value + 12 else it.month.value }
            }.onEach {
                afterLoading()
            }.catch {
                handleError(it)
            }.collect {
                Timber.i("Loading attendance summary result: Success")
                view?.apply {
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                    updateDataSet(it)
                }
                analytics.logEvent(
                    "load_data",
                    "type" to "attendance_summary",
                    "items" to it.size,
                    "item_id" to subjectId
                )
            }
        }
    }

    private fun handleError(error: Throwable) {
        Timber.i("Loading attendance summary result: An exception occurred")
        errorHandler.dispatch(error)
    }

    private fun afterLoading() {
        view?.run {
            hideRefresh()
            showProgress(false)
            enableSwipe(true)
        }
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

    private fun loadSubjects() {
        Timber.i("Loading attendance summary subjects started")
        launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emitAll(subjectRepository.getSubjects(student, semester))
            }.onEach {
                subjects = it
            }.map {
                ArrayList(it.map { subject -> subject.name })
            }.catch {
                Timber.i("Loading attendance summary subjects result: An exception occurred")
                errorHandler.dispatch(it)
            }.collect {
                Timber.i("Loading attendance summary subjects result: Success")
                view?.run {
                    view?.updateSubjects(it)
                    showSubjects(true)
                }
            }
        }
    }
}
