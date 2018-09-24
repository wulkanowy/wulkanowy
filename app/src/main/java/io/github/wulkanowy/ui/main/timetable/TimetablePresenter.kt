package io.github.wulkanowy.ui.main.timetable

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.extension.isHolidays
import io.github.wulkanowy.utils.extension.toFormat
import io.github.wulkanowy.utils.getNearMonday
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import org.threeten.bp.LocalDate
import java.util.*
import javax.inject.Inject

class TimetablePresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersManager,
        private val timetableRepository: TimetableRepository,
        private val sessionRepository: SessionRepository
) : BasePresenter<TimetableView>(errorHandler) {

    var currentDate: LocalDate = getNearMonday(LocalDate.now())
        private set

    override fun attachView(view: TimetableView) {
        super.attachView(view)
        view.initView()
    }

    fun loadTimetableForPreviousWeek() = loadData(currentDate.minusDays(7).toEpochDay())

    fun loadTimetableForNextWeek() = loadData(currentDate.plusDays(7).toEpochDay())

    fun loadData(date: Long?, forceRefresh: Boolean = false) {
        this.currentDate = LocalDate.ofEpochDay(date ?: getNearMonday(currentDate).toEpochDay())
        if (currentDate.isHolidays()) return

        disposable.clear()
        disposable.add(sessionRepository.getSemesters()
                .map { selectSemester(it, -1) }
                .flatMap { timetableRepository.getTimetable(it, currentDate, forceRefresh) }
                .map { it.groupBy { exam -> exam.date }.toSortedMap() }
                .map { createTimetableItems(it) }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .doOnSubscribe {
                    view?.run {
                        showRefresh(forceRefresh)
                        showProgress(!forceRefresh)
                        if (!forceRefresh) showEmpty(false)
                        showContent(null == date && forceRefresh)
                        showPreButton(!currentDate.minusDays(7).isHolidays())
                        showNextButton(!currentDate.plusDays(7).isHolidays())
                        updateNavigationWeek("${currentDate.toFormat("dd.MM")}-${currentDate.plusDays(4).toFormat("dd.MM")}")
                    }
                }
                .doAfterSuccess {
                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                    }
                }
                .doFinally {
                    view?.run {
                        showRefresh(false)
                        showProgress(false)
                    }
                }
                .subscribe({ view?.updateData(it) }) { errorHandler.proceed(it) })
    }

    private fun createTimetableItems(items: Map<Date, List<Timetable>>): List<TimetableHeader> {
        return items.map {
            TimetableHeader().apply {
                date = it.key
                subItems = it.value.map { item ->
                    TimetableItem().apply { lesson = item }
                }
            }
        }
    }

    fun onTimetableItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is TimetableItem) view?.showTimetableDialog(item.lesson)
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
