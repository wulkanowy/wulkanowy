package io.github.wulkanowy.ui.main.exam

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.getNearMonday
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import org.threeten.bp.LocalDate
import java.util.*
import javax.inject.Inject

class ExamPresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersManager,
        private val examRepository: ExamRepository,
        private val sessionRepository: SessionRepository
) : BasePresenter<ExamView>(errorHandler) {

    var date: LocalDate = LocalDate.now()

    override fun attachView(view: ExamView) {
        super.attachView(view)
        view.initView()
    }

    fun loadData(forceRefresh: Boolean = false) {
        disposable.add(sessionRepository.getSemesters()
                .map { selectSemester(it, -1) }
                .flatMap {
                    view?.setNavDate(date)
                    examRepository.getExams(it, getNearMonday(date), forceRefresh)
                }
                .map { it.groupBy { exam -> exam.date }.toSortedMap() }
                .map { createExamItems(it) }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .doFinally {
                    view?.run {
                        showRefresh(false)
                        showProgress(false)
                    }
                }
                .doAfterSuccess {
                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                    }
                }
                .subscribe({ view?.updateData(it) }) { errorHandler.proceed(it) })
    }

    private fun createExamItems(items: Map<Date, List<Exam>>): List<ExamItem> {
        return items.flatMap {
            val header = ExamHeader().apply { date = it.key }
            it.value.reversed().map { item ->
                ExamItem(header, item)
            }
        }
    }

    fun onUpdateDataList(size: Int) {
        if (size != 0) view?.showProgress(false)
    }

    fun onExamItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is ExamItem) view?.showExamDialog(item.exam)
    }

    private fun selectSemester(semesters: List<Semester>, index: Int): Semester {
        return semesters.single { it.current }.let { currentSemester ->
            if (index == -1) currentSemester
            else semesters.single { semester ->
                semester.run {
                    semesterName - 1 == index && diaryId == currentSemester.diaryId
                }
            }
        }
    }
}
