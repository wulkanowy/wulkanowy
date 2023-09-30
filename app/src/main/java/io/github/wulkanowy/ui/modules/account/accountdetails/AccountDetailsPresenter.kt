package io.github.wulkanowy.ui.modules.account.accountdetails

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoView
import io.github.wulkanowy.utils.getCurrentOrLast
import io.github.wulkanowy.utils.isCurrent
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.willBe
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class AccountDetailsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository,
    private val syncManager: SyncManager
) : BasePresenter<AccountDetailsView>(errorHandler, studentRepository) {

    private var studentWithSemesters: StudentWithSemesters? = null

    private var selectedIndex = 0

    private var schoolYear = 0

    private lateinit var lastError: Throwable

    private var studentId: Long? = null

    fun onAttachView(view: AccountDetailsView, student: Student) {
        super.onAttachView(view)
        studentId = student.id

        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
        Timber.i("Account details view was initialized")
        loadData()
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onSemesterSwitch(): Boolean {
        if (!studentWithSemesters?.semesters.isNullOrEmpty()) {
            view?.showSemesterDialog(selectedIndex - 1, studentWithSemesters!!.semesters)
        }
        return true
    }

    fun onSemesterSelected(index: Int) {
        if (selectedIndex != index + 1) {
            Timber.i("Change semester in grade view to ${index + 1} from $selectedIndex")
            val semestersToChange = listOf(
                studentWithSemesters!!.semesters[index],
                studentWithSemesters!!.semesters[selectedIndex - 1]
            )
            if (
                (!semestersToChange[0].isCurrent() && semestersToChange[0].willBe && LocalDate.now().isHolidays) ||
                (semestersToChange[0].isCurrent() && !semestersToChange[0].willBe && !LocalDate.now().isHolidays)
            ) {
                preferencesRepository.previewText = ""
            } else {
                preferencesRepository.previewText =
                    semestersToChange[0].diaryName + " / " + semestersToChange[0].semesterName
                semestersToChange[0].current = true
            }
            semestersToChange[1].current = false
            changeSemester(semestersToChange)
        }
    }

    private fun changeSemester(
        semesters: List<Semester>
    ) {
        resourceFlow { semesterRepository.updateSemester(semesters) }
            .logResourceStatus("updating semester")
            .onResourceLoading {
                view?.run {
                    showProgress(true)
                    showContent(false)
                }
            }
            .onResourceSuccess {
                view?.run {
                    recreateMainView()
                }
            }
            .onResourceNotLoading { view?.showProgress(false) }
            .onResourceError(errorHandler::dispatch)
            .launch()
    }

    private fun loadData() {
        resourceFlow { studentRepository.getSavedStudentById(studentId ?: -1) }
            .logResourceStatus("loading account details view")
            .onResourceLoading {
                view?.run {
                    showProgress(true)
                    showContent(false)
                }
            }
            .onResourceSuccess {
                studentWithSemesters = it
                val current = it!!.semesters.getCurrentOrLast()
                schoolYear = current.schoolYear
                selectedIndex = it.semesters.indexOf(current) + 1
                view?.run {
                    setCurrentSemesterName(current.semesterId, schoolYear)
                    showAccountData(studentWithSemesters!!.student)
                    enableSelectStudentButton(!studentWithSemesters!!.student.isCurrent)
                    showContent(true)
                    showErrorView(false)
                }
            }
            .onResourceNotLoading { view?.showProgress(false) }
            .onResourceError(errorHandler::dispatch)
            .launch()
    }

    fun onAccountEditSelected() {
        studentWithSemesters?.let {
            view?.showAccountEditDetailsDialog(it.student)
        }
    }

    fun onStudentInfoSelected(infoType: StudentInfoView.Type) {
        studentWithSemesters?.let {
            view?.openStudentInfoView(infoType, it)
        }
    }

    fun onStudentSelect() {
        if (studentWithSemesters == null) return

        Timber.i("Select student ${studentWithSemesters!!.student.id}")

        resourceFlow { studentRepository.switchStudent(studentWithSemesters!!) }
            .logResourceStatus("change student")
            .onResourceSuccess { view?.recreateMainView() }
            .onResourceNotLoading { view?.popViewToMain() }
            .onResourceError(errorHandler::dispatch)
            .launch("switch")
    }

    fun onRemoveSelected() {
        Timber.i("Select remove account")
        view?.showLogoutConfirmDialog()
    }

    fun onLogoutConfirm() {
        if (studentWithSemesters == null) return

        resourceFlow {
            val studentToLogout = studentWithSemesters!!.student

            studentRepository.logoutStudent(studentToLogout)
            val students = studentRepository.getSavedStudents(false)

            if (studentToLogout.isCurrent && students.isNotEmpty()) {
                studentRepository.switchStudent(students[0])
            }

            students
        }
            .logResourceStatus("logout user")
            .onResourceSuccess {
                view?.run {
                    when {
                        it.isEmpty() -> {
                            Timber.i("Logout result: Open login view")
                            syncManager.stopSyncWorker()
                            openClearLoginView()
                        }
                        studentWithSemesters?.student?.isCurrent == true -> {
                            Timber.i("Logout result: Logout student and switch to another")
                            recreateMainView()
                        }
                        else -> {
                            Timber.i("Logout result: Logout student")
                            recreateMainView()
                        }
                    }
                }
            }
            .onResourceNotLoading {
                if (studentWithSemesters?.student?.isCurrent == true) {
                    view?.popViewToMain()
                } else {
                    view?.popViewToAccounts()
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch("logout")
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            lastError = error
            setErrorDetails(message)
            showErrorView(true)
            showContent(false)
            showProgress(false)
        }
    }
}
