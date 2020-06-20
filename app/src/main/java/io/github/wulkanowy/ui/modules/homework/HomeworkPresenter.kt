package io.github.wulkanowy.ui.modules.homework

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.repositories.homework.HomeworkRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.rxSingle
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.ofEpochDay
import timber.log.Timber
import javax.inject.Inject

class HomeworkPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val homeworkRepository: HomeworkRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<HomeworkView>(errorHandler, studentRepository, schedulers) {

    private var baseDate: LocalDate = LocalDate.now().nextOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    private lateinit var lastError: Throwable

    private var refreshJob: Job? = null

    private var loadingJob: Job? = null

    fun onAttachView(view: HomeworkView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData(ofEpochDay(date ?: baseDate.toEpochDay()))
        if (currentDate.isHolidays) setBaseDateOnHolidays()
        reloadView()
    }

    fun onPreviousDay() {
        loadData(currentDate.minusDays(7))
        reloadView()
    }

    fun onNextDay() {
        loadData(currentDate.plusDays(7))
        reloadView()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the homework")
        refreshData(currentDate)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        refreshData(currentDate)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onHomeworkItemSelected(homework: Homework) {
        Timber.i("Select homework item ${homework.id}")
        view?.showTimetableDialog(homework)
    }

    private fun setBaseDateOnHolidays() {
        disposable.add(rxSingle { studentRepository.getCurrentStudent() }
            .flatMap { rxSingle { semesterRepository.getCurrentSemester(it) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                baseDate = baseDate.getLastSchoolDayIfHoliday(it.schoolYear)
                currentDate = baseDate
                reloadNavigation()
            }) {
                Timber.i("Loading semester result: An exception occurred")
            })
    }

    private fun refreshData(date: LocalDate) {
        refreshJob?.cancel()
        refreshJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emit(homeworkRepository.refreshHomework(student, semester, date, date))
            }.onEach { afterLoading() }.catch { handleError(it) }.collect()
        }
    }

    private fun loadData(date: LocalDate) {
        Timber.i("Loading homework data started")
        currentDate = date

        loadingJob?.cancel()
        loadingJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emitAll(homeworkRepository.getHomework(student, semester, date, date))
            }.map {
                createHomeworkItem(it)
            }.onEach {
                afterLoading()
            }.catch {
                handleError(it)
            }.collect {
                Timber.i("Loading homework result: Success")
                view?.apply {
                    updateData(it)
                    showEmpty(it.isEmpty())
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                }
                analytics.logEvent(
                    "load_data",
                    "type" to "homework",
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
        }
    }

    private fun handleError(error: Throwable) {
        Timber.i("Loading homework result: An exception occurred")
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

    private fun createHomeworkItem(items: List<Homework>): List<HomeworkItem<*>> {
        return items.groupBy { it.date }.toSortedMap().map { (date, exams) ->
            listOf(HomeworkItem(date, HomeworkItem.ViewType.HEADER)) + exams.reversed().map { exam ->
                HomeworkItem(exam, HomeworkItem.ViewType.ITEM)
            }
        }.flatten()
    }

    private fun reloadView() {
        Timber.i("Reload homework view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            enableSwipe(false)
            showContent(false)
            showEmpty(false)
            showErrorView(false)
            clearData()
            reloadNavigation()
        }
    }

    private fun reloadNavigation() {
        view?.apply {
            showPreButton(!currentDate.minusDays(7).isHolidays)
            showNextButton(!currentDate.plusDays(7).isHolidays)
            updateNavigationWeek("${currentDate.monday.toFormattedString("dd.MM")} - " +
                currentDate.sunday.toFormattedString("dd.MM"))
        }
    }
}
