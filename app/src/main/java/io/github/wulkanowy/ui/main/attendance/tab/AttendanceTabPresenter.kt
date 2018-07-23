package io.github.wulkanowy.ui.main.attendance.tab

import io.github.wulkanowy.data.RepositoryContract
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.FabricUtils
import io.github.wulkanowy.utils.async.AbstractTask
import io.github.wulkanowy.utils.async.AsyncListeners
import io.github.wulkanowy.utils.getAppDateFormatter
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AttendanceTabPresenter @Inject constructor(repository: RepositoryContract) : BasePresenter<AttendanceTabContract.View>(repository),
        AttendanceTabContract.Presenter, AsyncListeners.OnRefreshListener, AsyncListeners.OnFirstLoadingListener {

    private var refreshTask: AbstractTask? = null

    private var loadingTask: AbstractTask? = null

    private var headerItems = mutableListOf<AttendanceHeader>()

    private var date: LocalDate? = null

    private var isFirstSight = false

    override fun attachView(view: AttendanceTabContract.View) {
        super.attachView(view)

        getView().showProgressBar(true)
        getView().showNoItem(false)
    }

    override fun onFragmentActivated(isSelected: Boolean) {
        if (!isFirstSight && isSelected && isViewAttached) {
            isFirstSight = true

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

        FabricUtils.logRefresh("Attendance", result, date?.format(getAppDateFormatter()))
    }

    override fun onDoInBackgroundLoading() {
        val isShowPresent = repository.sharedRepo.isShowAttendancePresent

        val lessons = repository.dbRepo.getAttendance(date).groupBy { it.date }.values

        if (lessons.isEmpty()) syncData()

        headerItems = lessons.map {
            val header = AttendanceHeader(Pair(it[0].date, it[0].dateText))

            header.subItems = it.map {
                AttendanceSubItem(header, it.setDescription(repository.resRepo.getAttendanceLessonDescription(it)))
            }.filter { !(!isShowPresent && it.lesson.presence) }

            header.isExpanded = false

            header
        }.filter { it.subItems.isNotEmpty() }.toMutableList()
    }

    override fun onCanceledLoadingAsync() {
        // do nothing
    }

    override fun onEndLoadingAsync(result: Boolean, exception: Exception?) {
        view.updateAdapterList(headerItems)
        view.showNoItem(headerItems.isEmpty())
        view.showProgressBar(false)
    }

    override fun setArgumentDate(date: LocalDate?) {
        this.date = date
    }

    private fun syncData() {
        repository.syncRepo.syncAttendance(0, date)
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
        cancelAsyncTasks()
        isFirstSight = false
        super.detachView()
    }
}
