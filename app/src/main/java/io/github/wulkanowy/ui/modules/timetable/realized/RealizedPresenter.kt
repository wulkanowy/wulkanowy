package io.github.wulkanowy.ui.modules.timetable.realized

import com.google.firebase.analytics.FirebaseAnalytics.Param.START_DATE
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.RealizedRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.ofEpochDay
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RealizedPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val realizedRepository: RealizedRepository,
    private val schedulers: SchedulersProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<RealizedView>(errorHandler) {

    lateinit var currentDate: LocalDate
        private set

    fun onAttachView(view: RealizedView, date: Long?) {
        super.onAttachView(view)
        Timber.i("Timetable is attached")
        view.initView()
        loadData(ofEpochDay(date ?: now().nextOrSameSchoolDay.toEpochDay()))
        reloadView()
    }

    fun onPreviousDay() {
        loadData(currentDate.previousSchoolDay)
        reloadView()
    }

    fun onNextDay() {
        loadData(currentDate.nextSchoolDay)
        reloadView()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the timetable")
        loadData(currentDate, true)
    }

    fun onRealizedItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is RealizedItem) {
            Timber.i("Select realized item ${item.realized.id}")
            view?.showRealizedDialog(item.realized)
        }
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        Timber.i("Loading timetable data started")
        currentDate = date
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .delay(200, TimeUnit.MILLISECONDS)
                .flatMap { realizedRepository.getRealized(it, currentDate, currentDate, forceRefresh) }
                .map { items -> items.map { RealizedItem(it) } }
                .map { items -> items.sortedBy { it.realized.number } }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        hideRefresh()
                        showProgress(false)
                    }
                }
                .subscribe({
                    Timber.i("Loading realized lessons result: Success")
                    view?.apply {
                        updateData(it)
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent("load_realized", "items" to it.size, "force_refresh" to forceRefresh, START_DATE to currentDate.toFormattedString("yyyy-MM-dd"))
                }) {
                    Timber.i("Loading realized lessons result: An exception occurred")
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                })
        }
    }

    private fun reloadView() {
        Timber.i("Reload timetable view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            showContent(false)
            showEmpty(false)
            clearData()
            showNextButton(!currentDate.plusDays(1).isHolidays)
            showPreButton(!currentDate.minusDays(1).isHolidays)
            updateNavigationDay(currentDate.toFormattedString("EEEE \n dd.MM.YYYY").capitalize())
        }
    }
}
