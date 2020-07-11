package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.Status
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.repositories.gradestatistics.GradeStatisticsRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
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
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class GradeStatisticsPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val gradeStatisticsRepository: GradeStatisticsRepository,
    private val subjectRepository: SubjectRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<GradeStatisticsView>(errorHandler, studentRepository, schedulers) {

    private var subjects = emptyList<Subject>()

    private var currentSemesterId = 0

    private var currentSubjectName: String = "Wszystkie"

    private lateinit var lastError: Throwable

    private var refreshJob: Job? = null

    private var loadingJob: Job? = null

    var currentType: ViewType = ViewType.PARTIAL
        private set

    fun onAttachView(view: GradeStatisticsView, type: ViewType?) {
        super.onAttachView(view)
        currentType = type ?: ViewType.PARTIAL
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        currentSemesterId = semesterId
        loadSubjects()
        if (forceRefresh) refreshDataByType(semesterId, currentType)
        else {
            view?.showErrorView(false)
            loadDataByType(semesterId, currentSubjectName, currentType)
        }
    }

    fun onParentViewReselected() {
        view?.run {
            if (!isViewEmpty) resetView()
        }
    }

    fun onParentViewChangeSemester() {
        view?.run {
            showProgress(true)
            enableSwipe(false)
            showRefresh(false)
            showContent(false)
            showErrorView(false)
            showEmpty(false)
            clearView()
        }
//        job.cancel()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the grade stats")
        view?.notifyParentRefresh()
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        view?.notifyParentRefresh()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onSubjectSelected(name: String?) {
        Timber.i("Select grade stats subject $name")
        view?.run {
            showContent(false)
            showProgress(true)
            enableSwipe(false)
            showEmpty(false)
            showErrorView(false)
            clearView()
        }
        (subjects.singleOrNull { it.name == name }?.name)?.let {
            if (it != currentSubjectName) loadDataByType(currentSemesterId, it, currentType)
        }
    }

    fun onTypeChange() {
        val type = view?.currentType ?: ViewType.POINTS
        Timber.i("Select grade stats semester: $type")
//        job.cancel()
        view?.run {
            showContent(false)
            showProgress(true)
            enableSwipe(false)
            showEmpty(false)
            showErrorView(false)
            clearView()
        }
        loadDataByType(currentSemesterId, currentSubjectName, type)
    }

    private fun loadSubjects() {
        flow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            emitAll(subjectRepository.getSubjects(student, semester))
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading grade stats subjects started")
                Status.SUCCESS -> {
                    subjects = it.data!!

                    Timber.i("Loading grade stats subjects result: Success")
                    view?.run {
                        view?.updateSubjects(ArrayList(it.data.map { subject -> subject.name }))
                        showSubjects(true)
                    }
                }
                Status.ERROR -> {
                    Timber.i("Loading grade stats subjects result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("subjects")
    }

    private fun refreshDataByType(semesterId: Int, type: ViewType) {
        refreshJob?.cancel()
        refreshJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semesters = semesterRepository.getSemesters(student)
                val semester = semesters.first { item -> item.semesterId == semesterId }

                emit(with(gradeStatisticsRepository) {
                    when (type) {
                        ViewType.SEMESTER -> refreshGradeStatistics(student, semester, true)
                        ViewType.PARTIAL -> refreshGradeStatistics(student, semester, false)
                        ViewType.POINTS -> refreshGradePointStatistics(student, semester)
                    }
                })
            }.onCompletion { afterLoading(semesterId) }.catch { handleErrors(it, semesterId) }.collect()
        }
    }

    private fun loadDataByType(semesterId: Int, subjectName: String, type: ViewType) {
        currentSubjectName = if (preferencesRepository.showAllSubjectsOnStatisticsList) "Wszystkie" else subjectName
        currentType = type

        Timber.i("Loading grade stats data started")

        loadingJob?.cancel()
        loadingJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semesters = semesterRepository.getSemesters(student)
                val semester = semesters.first { item -> item.semesterId == semesterId }

                emitAll(with(gradeStatisticsRepository) {
                    when (type) {
                        ViewType.SEMESTER -> getGradesStatistics(student, semester, currentSubjectName, true)
                        ViewType.PARTIAL -> getGradesStatistics(student, semester, currentSubjectName, false)
                        ViewType.POINTS -> getGradesPointsStatistics(student, semester, currentSubjectName)
                    }
                })
            }.onEach {
                afterLoading(semesterId)
            }.catch {
                handleErrors(it, semesterId)
            }.collect {
                Timber.i("Loading grade stats result: Success")
                view?.run {
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                    showErrorView(false)
                    updateData(it, preferencesRepository.gradeColorTheme, preferencesRepository.showAllSubjectsOnStatisticsList)
                    showSubjects(!preferencesRepository.showAllSubjectsOnStatisticsList)
                }
                analytics.logEvent(
                    "load_data",
                    "type" to "grade_statistics",
                    "items" to it.size
                )
            }
        }
    }

    private fun afterLoading(semesterId: Int) {
        view?.run {
            showRefresh(false)
            showProgress(false)
            enableSwipe(true)
            notifyParentDataLoaded(semesterId)
        }
    }

    private fun handleErrors(error: Throwable, semesterId: Int) {
        Timber.i("Loading grade stats result: An exception occurred")
        errorHandler.dispatch(error)
        afterLoading(semesterId)
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
