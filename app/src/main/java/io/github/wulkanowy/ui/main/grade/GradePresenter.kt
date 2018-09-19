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

    override fun attachView(view: GradeView) {
        super.attachView(view)
        disposable.add(Completable.timer(150, TimeUnit.MILLISECONDS, schedulers.mainThread())
                .subscribe {
                    view.run {
                        initView()
                        showContent(true)
                        showProgress(false)
                    }
                    loadData()
                })
    }

    fun onSemesterSwitch(): Boolean {
        if (semesters.isNotEmpty()) view?.showSemesterDialog(selectedIndex)
        return true
    }

    fun changeSemester(index: Int) {
        if (selectedIndex != index) {
            selectedIndex = index
            semesters.first { item -> item.semesterName == index + 1 }
                    .let { view?.loadChildViewData(it.semesterId) }
        }
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
                .subscribe({ view?.loadChildViewData(it.semesterId) }) { errorHandler.proceed(it) })
    }
}

