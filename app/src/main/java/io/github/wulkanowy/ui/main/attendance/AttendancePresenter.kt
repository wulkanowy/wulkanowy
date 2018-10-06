package io.github.wulkanowy.ui.main.attendance

import android.os.Handler
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.AttendanceRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AttendancePresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersManager,
        private val attendanceRepository: AttendanceRepository,
        private val sessionRepository: SessionRepository
) : BasePresenter<AttendanceView>(errorHandler) {

    lateinit var currentDate: LocalDate
        private set

    fun onAttachView(view: AttendanceView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        loadData(LocalDate.ofEpochDay(date ?: LocalDate.now().nextOrSameSchoolDay.toEpochDay()))
    }

    fun onPreviousDay() {
        view?.apply {
            clearData()
            showProgress(true)
            showContent(false)
            showEmpty(false)
        }
        loadData(currentDate.previousSchoolDay)
    }

    fun onNextDay() {
        view?.apply {
            clearData()
            showProgress(true)
            showContent(false)
            showEmpty(false)
        }
        loadData(currentDate.nextSchoolDay)
    }

    fun onSwipeRefresh() {
        loadData(currentDate, true)
    }

    fun onAttendanceItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is AttendanceItem) view?.showAttendanceDialog(item.attendance)
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        currentDate = date
        disposable.apply {
            clear()
            add(sessionRepository.getSemesters()
                    .map { it.single { semester -> semester.current } }
                    .flatMap { attendanceRepository.getAttendance(it, date, date, forceRefresh) }
                    .map { items -> items.map { AttendanceItem(it) } }
                    .subscribeOn(schedulers.backgroundThread())
                    .observeOn(schedulers.mainThread())
                    .doFinally {
                        view?.run {
                            hideRefresh()
                            showProgress(false)
                        }
                    }
                    .subscribe({
                        view?.apply {
                            updateData(it)
                            Handler().postDelayed({
                                showEmpty(it.isEmpty())
                                showContent(it.isNotEmpty())
                            }, 300)

                        }
                    }) {
                        view?.run { showEmpty(isViewEmpty()) }
                        errorHandler.proceed(it)
                    }
            )
        }
    }
}
