package io.github.wulkanowy.ui.modules.attendance

import android.annotation.SuppressLint
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.repositories.attendance.AttendanceRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousOrSameSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.rxSingle
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.ofEpochDay
import timber.log.Timber
import javax.inject.Inject

class AttendancePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val semesterRepository: SemesterRepository,
    private val prefRepository: PreferencesRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<AttendanceView>(errorHandler, studentRepository, schedulers) {

    private var baseDate: LocalDate = now().previousOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    private lateinit var lastError: Throwable

    private val attendanceToExcuseList = mutableListOf<Attendance>()

    private var refreshJob: Job? = null

    private var loadingJob: Job? = null

    private var excuseJob: Job? = null

    fun onAttachView(view: AttendanceView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Attendance view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData(ofEpochDay(date ?: baseDate.toEpochDay()))
        if (currentDate.isHolidays) setBaseDateOnHolidays()
        reloadView()
    }

    fun onPreviousDay() {
        view?.finishActionMode()
        attendanceToExcuseList.clear()
        loadData(currentDate.previousSchoolDay)
        reloadView()
    }

    fun onNextDay() {
        view?.finishActionMode()
        attendanceToExcuseList.clear()
        loadData(currentDate.nextSchoolDay)
        reloadView()
    }

    fun onPickDate() {
        view?.showDatePickerDialog(currentDate)
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        loadData(LocalDate.of(year, month, day))
        reloadView()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the attendance")
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

    fun onViewReselected() {
        Timber.i("Attendance view is reselected")
        view?.also { view ->
            if (view.currentStackSize == 1) {
                baseDate.also {
                    if (currentDate != it) {
                        loadData(it)
                        reloadView()
                    } else if (!view.isViewEmpty) view.resetView()
                }
            } else view.popView()
        }
    }

    fun onMainViewChanged() {
        view?.finishActionMode()
    }

    fun onAttendanceItemSelected(attendance: Attendance) {
        view?.apply {
            if (!excuseActionMode) {
                Timber.i("Select attendance item ${attendance.id}")
                showAttendanceDialog(attendance)
            }
        }
    }

    fun onExcuseButtonClick() {
        view?.startActionMode()
    }

    fun onExcuseCheckboxSelect(attendanceItem: Attendance, checked: Boolean) {
        if (checked) attendanceToExcuseList.add(attendanceItem)
        else attendanceToExcuseList.remove(attendanceItem)
    }

    fun onExcuseSubmitButtonClick(): Boolean {
        view?.apply {
            return if (attendanceToExcuseList.isNotEmpty()) {
                showExcuseDialog()
                true
            } else {
                showMessage(excuseNoSelectionString)
                false
            }
        }
        return false
    }

    fun onExcuseDialogSubmit(reason: String) {
        view?.finishActionMode()
        excuseAbsence(if (reason != "") reason else null, attendanceToExcuseList.toList())
    }

    fun onPrepareActionMode(): Boolean {
        view?.apply {
            showExcuseCheckboxes(true)
            showExcuseButton(false)
        }
        attendanceToExcuseList.clear()
        return true
    }

    fun onDestroyActionMode() {
        view?.apply {
            showExcuseCheckboxes(false)
            showExcuseButton(true)
        }
    }

    fun onSummarySwitchSelected(): Boolean {
        view?.openSummaryView()
        return true
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

    private fun refreshData() {
        refreshJob?.cancel()
        refreshJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emit(attendanceRepository.refreshAttendance(student, semester, currentDate, currentDate))
            }.onCompletion { afterLoading() }.catch { handleError(it) }.collect()
        }
    }

    private fun loadData(date: LocalDate) {
        Timber.i("Loading attendance data started")
        currentDate = date

        loadingJob?.cancel()
        loadingJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emitAll(attendanceRepository.getAttendance(student, semester, date, date))
            }.map {
                if (prefRepository.isShowPresent) it
                else it.filter { item -> !item.presence }
            }.map { it.sortedBy { item -> item.number } }
                .onStart { view?.showExcuseButton(false) }
                .onEach { afterLoading() }
                .catch { handleError(it) }
                .collect {
                    Timber.i("Loading attendance result: Success")
                    view?.apply {
                        updateData(it)
                        showEmpty(it.isEmpty())
                        showErrorView(false)
                        showContent(it.isNotEmpty())
                        showExcuseButton(it.any { item -> item.excusable })
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "attendance",
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
        Timber.i("Loading attendance result: An exception occurred")
        errorHandler.dispatch(error)
        afterLoading()
    }

    private fun excuseAbsence(reason: String?, toExcuseList: List<Attendance>) {
        Timber.i("Excusing absence started")

        excuseJob?.cancel()
        excuseJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emit(attendanceRepository.excuseForAbsence(student, semester, toExcuseList, reason))
            }.onStart {
                view?.apply {
                    showProgress(true)
                    showContent(false)
                    showExcuseButton(false)
                }
            }.catch {
                Timber.i("Excusing for absence result: An exception occurred")
                view?.showProgress(false)
                errorHandler.dispatch(it)
            }.collect {
                Timber.i("Excusing for absence result: Success")
                analytics.logEvent("excuse_absence", "items" to attendanceToExcuseList.size)
                attendanceToExcuseList.clear()
                view?.apply {
                    showExcuseButton(false)
                    showMessage(excuseSuccessString)
                    showContent(true)
                    showProgress(false)
                }
                loadData(currentDate)
                refreshData()
            }
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

    private fun reloadView() {
        Timber.i("Reload attendance view with the date ${currentDate.toFormattedString()}")
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

    @SuppressLint("DefaultLocale")
    private fun reloadNavigation() {
        view?.apply {
            showPreButton(!currentDate.minusDays(1).isHolidays)
            showNextButton(!currentDate.plusDays(1).isHolidays)
            updateNavigationDay(currentDate.toFormattedString("EEEE, dd.MM").capitalize())
        }
    }
}
