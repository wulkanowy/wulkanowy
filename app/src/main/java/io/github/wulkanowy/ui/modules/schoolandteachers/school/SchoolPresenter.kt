package io.github.wulkanowy.ui.modules.schoolandteachers.school

import io.github.wulkanowy.data.repositories.SchoolRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.catch
import io.github.wulkanowy.utils.logStatus
import io.github.wulkanowy.utils.onSuccess
import io.github.wulkanowy.utils.withErrorHandler
import timber.log.Timber
import javax.inject.Inject

class SchoolPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schoolRepository: SchoolRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<SchoolView>(errorHandler, studentRepository) {

    private var address: String? = null

    private var contact: String? = null

    private lateinit var lastError: Throwable

    override fun onAttachView(view: SchoolView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("School view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
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

    fun onParentViewLoadData(forceRefresh: Boolean) {
        loadData(forceRefresh)
    }

    fun onAddressSelected() {
        address?.let { view?.openMapsLocation(it) }
    }

    fun onTelephoneSelected() {
        contact?.let { view?.dialPhone(it) }
    }

    private fun loadData(forceRefresh: Boolean = false) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            schoolRepository.getSchoolInfo(student, semester, forceRefresh)
        }.logStatus("load school info").withErrorHandler(errorHandler).onSuccess {
            if (it != null) {
                view?.run {
                    address = it.address.ifBlank { null }
                    contact = it.contact.ifBlank { null }
                    updateData(it)
                    showContent(true)
                    showEmpty(false)
                    showErrorView(false)
                }
                analytics.logEvent("load_item", "type" to "school")
            } else view?.run {
                Timber.i("Loading school result: No school info found")
                showContent(!isViewEmpty)
                showEmpty(isViewEmpty)
                showErrorView(false)
            }
        }.afterLoading {
            view?.run {
                hideRefresh()
                showProgress(false)
                enableSwipe(true)
                notifyParentDataLoaded()
            }
        }.catch {
            errorHandler.dispatch(it)
            view?.notifyParentDataLoaded()
        }.launch()
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
                showContent(false)
                showProgress(false)
            } else showError(message, error)
        }
    }
}
