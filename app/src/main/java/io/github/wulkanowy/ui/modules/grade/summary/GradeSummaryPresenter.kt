package io.github.wulkanowy.ui.modules.grade.summary

import io.github.wulkanowy.Status
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.grade.GradeAverageProvider
import io.github.wulkanowy.ui.modules.grade.GradeDetailsWithAverage
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class GradeSummaryPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val gradeRepository: GradeRepository,
    private val averageProvider: GradeAverageProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<GradeSummaryView>(errorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: GradeSummaryView) {
        super.onAttachView(view)
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        Timber.i("Loading grade summary data started")

        if (forceRefresh) refreshData(semesterId)
        else {
            view?.showErrorView(false)
            loadData(semesterId)
        }
    }

    private fun refreshData(semesterId: Int) {
        flowWithResource {
            val student = studentRepository.getCurrentStudent()
            val semesters = semesterRepository.getSemesters(student)
            val semester = semesters.first { item -> item.semesterId == semesterId }
            gradeRepository.refreshGrades(student, semester)
        }.onEach {
            if (it.status == Status.ERROR) handleError(it.error!!, semesterId)
        }.afterLoading {
            afterLoading(semesterId)
        }.launch("refresh")
    }

    private fun loadData(semesterId: Int) {
        flow {
            val student = studentRepository.getCurrentStudent()
            emitAll(averageProvider.getGradesDetailsWithAverage(student, semesterId))
        }.onEach {
            afterLoading(semesterId)
        }.catch {
            handleError(it, semesterId)
        }.onEach {
            Timber.i("Loading grade summary result: Success")
            view?.run {
                showEmpty(it.isEmpty())
                showContent(it.isNotEmpty())
                showErrorView(false)
                updateData(createGradeSummaryItems(it))
            }
            analytics.logEvent(
                "load_data",
                "type" to "grade_summary",
                "items" to it.size
            )
        }.launch()
    }

    private fun afterLoading(semesterId: Int) {
        view?.run {
            showRefresh(false)
            showProgress(false)
            enableSwipe(true)
            notifyParentDataLoaded(semesterId)
        }
    }

    private fun handleError(error: Throwable, semesterId: Int) {
        Timber.i("Loading grade summary result: An exception occurred")
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

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the grade summary")
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
            showEmpty(false)
            clearView()
        }
        cancelJobs("load")
    }

    private fun createGradeSummaryItems(items: List<GradeDetailsWithAverage>): List<GradeSummary> {
        return items
            .filter { !checkEmpty(it) }
            .sortedBy { it.subject }
            .map { it.summary.copy(average = it.average) }
    }

    private fun checkEmpty(gradeSummary: GradeDetailsWithAverage): Boolean {
        return gradeSummary.run {
            summary.finalGrade.isBlank()
                && summary.predictedGrade.isBlank()
                && average == .0
                && points.isBlank()
        }
    }
}
