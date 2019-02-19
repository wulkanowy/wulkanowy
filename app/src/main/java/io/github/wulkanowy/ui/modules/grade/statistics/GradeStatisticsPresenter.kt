package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.data.repositories.gradestatistics.GradeStatisticsRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class GradeStatisticsPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val gradeStatisticsRepository: GradeStatisticsRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schedulers: SchedulersProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<GradeStatisticsView>(errorHandler) {

    private var currentSemesterId = 0

    override fun onAttachView(view: GradeStatisticsView) {
        super.onAttachView(view)
        view.initView()
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        currentSemesterId = semesterId
        loadData(semesterId, forceRefresh)
    }

    fun onParentViewChangeSemester() {
        view?.run {
//            showProgress(true)
//            showRefresh(false)
//            showContent(false)
//            showEmpty(false)
//            clearView()
        }
        disposable.clear()
    }

    private fun loadData(semesterId: Int, forceRefresh: Boolean) {
        Timber.i("Loading grade stats data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .flatMap { gradeStatisticsRepository.getGradesStatistics(it.first { item -> item.semesterId == semesterId }, "Język polski", forceRefresh) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
//                    showRefresh(false)
//                    showProgress(false)
                    notifyParentDataLoaded(semesterId)
                }
            }
            .subscribe({
                Timber.i("Loading grade stats result: Success")
                view?.run {
//                    showEmpty(it.isEmpty())
//                    showContent(it.isNotEmpty())
                    updateData(it)
                }
                analytics.logEvent("load_grade_details", "items" to it.size, "force_refresh" to forceRefresh)
            }) {
                Timber.i("Loading grade stats result: An exception occurred")
//                view?.run { showEmpty(isViewEmpty) }
                errorHandler.dispatch(it)
            })
    }
}
