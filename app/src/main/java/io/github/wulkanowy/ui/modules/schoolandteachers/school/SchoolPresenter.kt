package io.github.wulkanowy.ui.modules.schoolandteachers.school

import io.github.wulkanowy.data.repositories.school.SchoolRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
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

    override fun onAttachView(view: SchoolView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Teacher view was initialized")
        loadData()
    }

    fun onSwipeRefresh() {
        loadData(true)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        loadData(forceRefresh)
    }


    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading teachers data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMapMaybe { schoolRepository.getSchoolInfo(it, forceRefresh) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    hideRefresh()
                    showProgress(false)
                    enableSwipe(true)
                    notifyParentDataLoaded()
                }
            }.subscribe({
                Timber.i("Loading teachers result: Success")
                view?.run {
                    updateData(it)
                    // TODO
//                    showContent(it.isNotEmpty())
//                    showEmpty(it.isEmpty())
                }
                analytics.logEvent("load_teachers","force_refresh" to forceRefresh)
            }) {
                Timber.i("Loading teachers result: An exception occurred")
                errorHandler.dispatch(it)
            })
    }
}
