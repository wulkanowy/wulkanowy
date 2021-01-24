package io.github.wulkanowy.ui.modules.studentinfo

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.StudentInfo
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentInfoRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class StudentInfoPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val studentInfoRepository: StudentInfoRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<StudentInfoView>(errorHandler, studentRepository) {

    private lateinit var infoType: StudentInfoView.Type

    private lateinit var lastError: Throwable

    fun onAttachView(view: StudentInfoView, type: StudentInfoView.Type) {
        super.onAttachView(view)
        infoType = type
        view.initView()
        Timber.i("Student info $infoType view was initialized")
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

    fun onItemSelected(position: Int) {
        if (infoType != StudentInfoView.Type.FAMILY) return

        if (position == 0) {
            view?.openStudentInfoView(StudentInfoView.Type.FIRST_GUARDIAN)
        } else {
            view?.openStudentInfoView(StudentInfoView.Type.SECOND_GUARDIAN)
        }
    }

    fun onItemLongClick(text: String) {
        view?.copyToClipboard(text)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            studentInfoRepository.getStudentInfo(student, semester, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading student info $infoType started")
                Status.SUCCESS -> {
                    if (it.data != null) {
                        Timber.i("Loading student info $infoType result: Success")
                        showCorrectData(it.data)
                        view?.run {
                            showContent(true)
                            showEmpty(false)
                            showErrorView(false)
                        }
                        analytics.logEvent("load_item", "type" to "student_info")
                    } else {
                        Timber.i("Loading student info $infoType result: No school info found")
                        view?.run {
                            showContent(!isViewEmpty)
                            showEmpty(isViewEmpty)
                            showErrorView(false)
                        }
                    }
                }
                Status.ERROR -> {
                    Timber.i("Loading student info $infoType result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                hideRefresh()
                showProgress(false)
                enableSwipe(true)
            }
        }.launch()
    }

    private fun showCorrectData(studentInfo: StudentInfo) {
        when (infoType) {
            StudentInfoView.Type.PERSONAL -> view?.showPersonalTypeData(studentInfo)
            StudentInfoView.Type.CONTACT -> view?.showContactTypeData(studentInfo)
            StudentInfoView.Type.ADDRESS -> view?.showAddressTypeData(studentInfo)
            StudentInfoView.Type.FAMILY -> view?.showFamilyTypeData(studentInfo)
            StudentInfoView.Type.SECOND_GUARDIAN -> view?.showSecondGuardianTypeData(studentInfo)
            StudentInfoView.Type.FIRST_GUARDIAN -> view?.showFirstGuardianTypeData(studentInfo)
        }
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
                showContent(false)
            } else showError(message, error)
        }
    }
}