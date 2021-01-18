package io.github.wulkanowy.ui.modules.studentinfo

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.StudentInfo
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentInfoRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
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

    fun onAttachView(view: StudentInfoView, type: StudentInfoView.Type) {
        super.onAttachView(view)
        infoType = type
        view.initView()
        Timber.i("School view was initialized")
        loadData()
    }

    fun onItemSelected(position: Int) {
        if (infoType != StudentInfoView.Type.FAMILY) return

        if (position == 0) {
            view?.openStudentInfoView(StudentInfoView.Type.FIRST_GUARDIAN)
        } else {
            view?.openStudentInfoView(StudentInfoView.Type.SECOND_GUARDIAN)
        }
    }

    private fun loadData(forceRefresh: Boolean = false) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            studentInfoRepository.getStudentInfo(student, semester, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading student info started")
                Status.SUCCESS -> {
                    showCorrectData(it.data!!)
                    analytics.logEvent(
                        "load_item",
                        "type" to "student_info"
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading student info result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch()
    }

    private fun showCorrectData(studentInfo: StudentInfo) {
        when (infoType) {
            StudentInfoView.Type.PERSONAL -> view?.showPersonalTypeData(studentInfo)
            StudentInfoView.Type.CONTACT -> view?.showContactTypeData(studentInfo)
            StudentInfoView.Type.ADDRESS -> view?.showPersonalTypeData(studentInfo)
            StudentInfoView.Type.FAMILY -> view?.showFamilyTypeData(studentInfo)
            StudentInfoView.Type.SECOND_GUARDIAN -> view?.showSecondGuardianTypeData(studentInfo)
            StudentInfoView.Type.FIRST_GUARDIAN -> view?.showFirstGuardianTypeData(studentInfo)
        }
    }
}