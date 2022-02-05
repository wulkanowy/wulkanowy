package io.github.wulkanowy.ui.modules.schoolannouncement

import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.repositories.SchoolAnnouncementRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.logStatus
import io.github.wulkanowy.utils.onData
import io.github.wulkanowy.utils.onError
import io.github.wulkanowy.utils.onSuccess
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class SchoolAnnouncementPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val analytics: AnalyticsHelper,
    private val schoolAnnouncementRepository: SchoolAnnouncementRepository,
) : BasePresenter<SchoolAnnouncementView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: SchoolAnnouncementView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("School announcement view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the School announcement")
        loadData(true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onItemClickListener(item: SchoolAnnouncement) {
        view?.openSchoolAnnouncementDialog(item)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            schoolAnnouncementRepository.getSchoolAnnouncements(student, forceRefresh)
        }
            .logStatus("load school announcement").onEach {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                }
            }
            .onData {
                view?.run {
                    showRefresh(true)
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    updateData(it)
                }
            }
            .afterLoading {
                view?.showRefresh(false)
            }
            .onSuccess {
                analytics.logEvent(
                    "load_school_announcement",
                    "items" to it.size
                )
            }
            .onError(errorHandler::dispatch)
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
            } else showError(message, error)
        }
    }
}
