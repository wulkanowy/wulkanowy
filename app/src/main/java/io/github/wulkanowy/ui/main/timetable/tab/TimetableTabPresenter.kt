package io.github.wulkanowy.ui.main.timetable.tab

import io.github.wulkanowy.data.RepositoryContract
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.*
import io.github.wulkanowy.utils.async.AbstractTask
import io.github.wulkanowy.utils.async.AsyncListeners
import org.threeten.bp.LocalDate
import javax.inject.Inject

class TimetableTabPresenter @Inject constructor(repository: RepositoryContract) : BasePresenter<TimetableTabContract.View>(repository), TimetableTabContract.Presenter, AsyncListeners.OnRefreshListener, AsyncListeners.OnFirstLoadingListener {

    private var refreshTask: AbstractTask? = null

    private var loadingTask: AbstractTask? = null

    private var headerItems = mutableListOf<TimetableHeader>()

    private var date: LocalDate? = null

    private val freeWeekName: String? = null

    private var isFirstSight = false

    override fun attachView(view: TimetableTabContract.View) {
        super.attachView(view)
        getView().showProgressBar(true)
        getView().showNoItem(false)
    }

    override fun onFragmentActivated(isSelected: Boolean) {
        if (!isFirstSight && isSelected && isViewAttached) {
            loadingTask = AbstractTask()
            loadingTask!!.setOnFirstLoadingListener(this)
            loadingTask!!.execute()
        } else if (!isSelected) {
            cancelAsyncTasks()
        }
    }

    override fun onRefresh() {
        if (view.isNetworkConnected) {
            refreshTask = AbstractTask()
            refreshTask!!.setOnRefreshListener(this)
            refreshTask!!.execute()
        } else {
            view.showNoNetworkMessage()
            view.hideRefreshingBar()
        }
    }

    override fun onDoInBackgroundRefresh() {
        syncData()
    }

    override fun onCanceledRefreshAsync() {
        if (isViewAttached) {
            view.hideRefreshingBar()
        }
    }

    override fun onEndRefreshAsync(result: Boolean, exception: Exception?) {
        if (result) {
            loadingTask = AbstractTask()
            loadingTask!!.setOnFirstLoadingListener(this)
            loadingTask!!.execute()

            view.onRefreshSuccess()
        } else {
            view.showMessage(repository.resRepo.getErrorLoginMessage(exception))
        }
        view.hideRefreshingBar()

        FabricUtils.logRefresh("Timetable", result, date!!.format(getAppDateFormatter()))
    }

    private fun getTimetableDays(date: LocalDate?) = repository.dbRepo.getTimetable(date).groupBy { it.date }.values

    override fun onDoInBackgroundLoading() {
        var days = getTimetableDays(date)
        if (days.isEmpty()) {
            syncData()
            days = getTimetableDays(date)
        }

        headerItems = days.map {
            val header = TimetableHeader(android.util.Pair(it[0].date.format(getAppDateFormatter()), it[0].freeDayName))

            header.subItems = it.map {
                TimetableSubItem(header, it)
            }

            header.isExpanded = false

            header
        }.toMutableList()
    }

    override fun onCanceledLoadingAsync() {
        // do nothing
    }

    override fun onEndLoadingAsync(result: Boolean, exception: Exception?) {
        view.showNoItem(headerItems.isEmpty())
        view.updateAdapterList(headerItems)

        if (headerItems.isEmpty()) {
            view.setFreeWeekName(freeWeekName)
        } else {
            expandCurrentDayHeader()
        }
        view.showProgressBar(false)
        isFirstSight = true
    }

    private fun expandCurrentDayHeader() {
        val monday = getParsedDate(date!!.format(getAppDateFormatter()), AppConstant.DATE_PATTERN)

        if (isDateInWeek(monday, LocalDate.now()) && !isFirstSight) {
            view.expandItem(LocalDate.now().dayOfWeek.value - 1)
        }
    }

    override fun setArgumentDate(date: LocalDate) {
        this.date = date
    }

    private fun syncData() {
        repository.syncRepo.syncTimetable(0, date)
    }

    private fun cancelAsyncTasks() {
        if (refreshTask != null) {
            refreshTask!!.cancel(true)
            refreshTask = null
        }
        if (loadingTask != null) {
            loadingTask!!.cancel(true)
            loadingTask = null
        }
    }

    override fun detachView() {
        isFirstSight = false
        cancelAsyncTasks()
        super.detachView()
    }
}
