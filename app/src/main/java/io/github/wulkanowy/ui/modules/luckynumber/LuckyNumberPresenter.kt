package io.github.wulkanowy.ui.modules.luckynumber

import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class LuckyNumberPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val luckyNumberRepository: LuckyNumberRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LuckyNumberView>(errorHandler) {

    override fun onAttachView(view: LuckyNumberView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    private fun loadData(forceRefresh: Boolean = false) {
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .flatMap { luckyNumberRepository.getLuckyNumbers(it, forceRefresh) }
                .map { items -> items[0] }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        hideRefresh()
                        showProgress(false)
                    }
                }
                .subscribe({
                    view?.apply {
                        updateData(it)
                        showContent(it.luckyNumber != 0)
                        showEmpty(it.luckyNumber == 0)
                    }
                    analytics.logEvent("load_lucky_number", mapOf("lucky_number" to it.luckyNumber, "force_refresh" to forceRefresh))
                }) {
                    errorHandler.dispatch(it)
                })
        }
    }

    fun onSwipeRefresh() {
        loadData(true)
    }
}