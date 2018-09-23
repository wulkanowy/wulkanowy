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

    private val loadedSemesterId = mutableMapOf<Int, String>()

    override fun attachView(view: GradeView) {
        super.attachView(view)
        disposable.add(Completable.timer(150, TimeUnit.MILLISECONDS, schedulers.mainThread())
                .subscribe {
                    view.initView()
                    loadData()
                })
    }

    fun onViewReselected() {
        view?.run { notifyChildParentReselected(currentPageIndex()) }
    }

    fun onSemesterSwitch(): Boolean {
        if (semesters.isNotEmpty()) view?.showSemesterDialog(selectedIndex)
        return true
    }

    fun onSemesterSelected(index: Int) {
        if (selectedIndex != index) {
            selectedIndex = index
            view?.let { loadChild(it.currentPageIndex(), showProgress = true) }
        }
    }

    fun onChildViewRefresh() {
        view?.let { loadChild(it.currentPageIndex(), forceRefresh = true) }
    }

    fun onChildViewLoaded(semesterId: String) {
        view?.apply {
            showContent(true)
            showProgress(false)
            loadedSemesterId[currentPageIndex()] = semesterId
        }
    }

    fun onPageSelected(index: Int) {
        loadChild(index)
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
                .subscribe({ _ ->
                    view?.let { loadChild(it.currentPageIndex(), showProgress = true) }
                }) { errorHandler.proceed(it) })
    }

    private fun loadChild(index: Int, forceRefresh: Boolean = false, showProgress: Boolean = false) {
        semesters.first { it.semesterName == selectedIndex + 1 }.semesterId.also {
            if (forceRefresh || loadedSemesterId[index] != it) {
                if (showProgress) showChildrenProgress(true)
                view?.loadChildData(it, forceRefresh, index)
            } else showChildrenProgress(false)
        }
    }

    private fun showChildrenProgress(showProgress: Boolean) {
        for (i in 0..1) view?.showChildProgress(i, showProgress)
    }
}

