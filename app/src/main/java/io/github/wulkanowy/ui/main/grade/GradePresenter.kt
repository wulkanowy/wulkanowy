package io.github.wulkanowy.ui.main.grade

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.reactivex.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GradePresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersManager,
        private val sessionRepository: SessionRepository) : BasePresenter<GradeView>(errorHandler) {

    private var semesters = emptyList<Semester>()

    private var selectedIndex = 0

    private val loadedSemesterId = mutableListOf<String>()

    override fun attachView(view: GradeView) {
        super.attachView(view)
        disposable.add(Completable.timer(150, TimeUnit.MILLISECONDS, schedulers.mainThread())
                .subscribe {
                    view.initView()
                    loadData()
                })
    }

    fun onSemesterSwitch(): Boolean {
        if (semesters.isNotEmpty()) view?.showSemesterDialog(selectedIndex)
        return true
    }

    fun onSemesterSelected(index: Int) {
        if (selectedIndex != index) {
            selectedIndex = index
            view?.run {
                showChildProgress()
                loadChild(false, currentPageIndex())
            }
        }
    }

    fun onChildViewRefresh() {
        view?.run { loadChild(true, currentPageIndex()) }
    }

    fun onFirstViewLoaded() {
        view?.run {
            showContent(true)
            showProgress(false)
        }
    }

    fun onPageSelected(index: Int) {
        loadChild(false, index)
    }

    private fun loadData() {
        disposable.add(sessionRepository.getSemesters()
                .map {
                    it.first { item -> item.current }.also { current ->
                        selectedIndex = current.semesterName - 1
                        semesters = it.filter { semester -> semester.diaryId == current.diaryId }
                    }
                }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .subscribe({ loadChild(false, 0) })
                { errorHandler.proceed(it) })
    }

    private fun loadChild(forceRefresh: Boolean, index: Int) {
        semesters.first { it.semesterName == selectedIndex + 1 }.semesterId.let {
            if (forceRefresh || loadedSemesterId.getOrNull(index) != it) {
                view?.loadChildViewData(it, forceRefresh, index)
                loadedSemesterId.add(index, it)
            }
        }
    }
}

