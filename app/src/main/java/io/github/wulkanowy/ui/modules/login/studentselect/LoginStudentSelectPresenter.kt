package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.mappers.mapToStudentWithSemesters
import io.github.wulkanowy.data.pojos.RegisterStudent
import io.github.wulkanowy.data.pojos.RegisterTeacher
import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LoginStudentSelectPresenter @Inject constructor(
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val syncManager: SyncManager,
    private val analytics: AnalyticsHelper,
    private val appInfo: AppInfo,
) : BasePresenter<LoginStudentSelectView>(loginErrorHandler, studentRepository) {

    private var lastError: Throwable? = null

    private lateinit var registerUser: RegisterUser
    private lateinit var students: List<StudentWithSemesters>
    private var isEmptySymbolsExpanded = false

    private val selectedSubjects = mutableListOf<LoginStudentSelectItem>()

    fun onAttachView(view: LoginStudentSelectView, registerUser: RegisterUser) {
        super.onAttachView(view)
        with(view) {
            initView()
            showContact(false)
            enableSignIn(false)
            loginErrorHandler.onStudentDuplicate = {
                showMessage(it)
                Timber.i("The student already registered in the app was selected")
            }
        }

        this.registerUser = registerUser
        loadData(registerUser)
    }

    fun onSignIn() {
        registerStudents(selectedSubjects)
    }

    private fun onEmptySymbolsToggle() {
        isEmptySymbolsExpanded = !isEmptySymbolsExpanded

        view?.updateData(createItems(registerUser, students))
    }

    private fun onItemSelected(item: LoginStudentSelectItem) {
        when (item) {
            is LoginStudentSelectItem.Student -> if (!item.isEnabled) return
            is LoginStudentSelectItem.Teacher -> if (!item.isEnabled) return
            else -> return
        }

        selectedSubjects
            .removeAll {
                if (it is LoginStudentSelectItem.Student && item is LoginStudentSelectItem.Student) {
                    it.student == item.student
                } else true
            }
            .let { if (!it) selectedSubjects.add(item) }

        view?.enableSignIn(selectedSubjects.isNotEmpty())
        view?.updateData(createItems(registerUser, students))
    }

    private fun loadData(registerUser: RegisterUser) {
        resetSelectedState()

        resourceFlow { studentRepository.getSavedStudents(false) }.onEach {
            students = it.dataOrNull.orEmpty()
            when (it) {
                is Resource.Loading -> Timber.d("Login student select students load started")
                is Resource.Success -> view?.updateData(createItems(registerUser, it.data))
                is Resource.Error -> {
                    errorHandler.dispatch(it.error)
                    lastError = it.error
                    view?.updateData(createItems(registerUser, it.dataOrNull.orEmpty()))
                }
            }
        }.launch()
    }

    private fun createItems(
        registerUser: RegisterUser,
        students: List<StudentWithSemesters>,
    ): List<LoginStudentSelectItem> = buildList {
        registerUser.symbols.filter { it.schools.isNotEmpty() }.forEach { registerSymbol ->
            add(LoginStudentSelectItem.SymbolHeader(registerSymbol))

            registerSymbol.schools.forEach { registerUnit ->
                add(LoginStudentSelectItem.SchoolHeader(registerUnit))

                registerUnit.subjects.forEach { subject ->
                    when (subject) {
                        is RegisterStudent -> add(
                            LoginStudentSelectItem.Student(
                                symbol = registerSymbol,
                                unit = registerUnit,
                                student = subject,
                                onClick = ::onItemSelected,
                                isEnabled = students.none {
                                    it.student.email == registerUser.login
                                        && it.student.symbol == registerSymbol.symbol
                                        && it.student.studentId == subject.studentId
                                        && it.student.schoolSymbol == registerUnit.schoolId
                                        && it.student.classId == subject.classId
                                },
                                isSelected = subject in selectedSubjects.mapNotNull {
                                    if (it is LoginStudentSelectItem.Student) it.student else null // todo
                                },
                            )
                        )
                        is RegisterTeacher -> add(
                            LoginStudentSelectItem.Teacher(
                                symbol = registerSymbol,
                                unit = registerUnit,
                                teacher = subject,
                                onClick = ::onItemSelected,
                                isEnabled = true, // todo
                                isSelected = subject in selectedSubjects.mapNotNull {
                                    if (it is LoginStudentSelectItem.Teacher) it.teacher else null // todo
                                },
                            )
                        )
                    }
                }
            }
        }

        if (registerUser.symbols.any { it.schools.isNotEmpty() }) {
            add(
                LoginStudentSelectItem.EmptySymbolsHeader(
                    isExpanded = isEmptySymbolsExpanded,
                    onClick = ::onEmptySymbolsToggle,
                )
            )

            registerUser.symbols
                .filter { isEmptySymbolsExpanded && it.schools.isEmpty() }
                .forEach { registerSymbol ->
                    add(LoginStudentSelectItem.SymbolHeader(registerSymbol))
                }
        }
    }

    private fun resetSelectedState() {
        selectedSubjects.clear()
        view?.enableSignIn(false)
    }

    private fun registerStudents(subjects: List<LoginStudentSelectItem>) {
        val studentsWithSemesters = subjects
            .filterIsInstance<LoginStudentSelectItem.Student>().map { item ->
                item.student.mapToStudentWithSemesters(
                    user = registerUser,
                    symbol = item.symbol,
                    unit = item.unit,
                    colors = appInfo.defaultColorsForAvatar,
                )
            }
        resourceFlow { studentRepository.saveStudents(studentsWithSemesters) }
            .logResourceStatus("registration")
            .onEach {
                when (it) {
                    is Resource.Loading -> view?.run {
                        showProgress(true)
                        showContent(false)
                    }
                    is Resource.Success -> {
                        syncManager.startOneTimeSyncWorker(quiet = true)
                        view?.navigateToNext()
                        logRegisterEvent(studentsWithSemesters)
                    }
                    is Resource.Error -> {
                        view?.apply {
                            showProgress(false)
                            showContent(true)
                            showContact(true)
                        }
                        lastError = it.error
                        loginErrorHandler.dispatch(it.error)
                        logRegisterEvent(studentsWithSemesters, it.error)
                    }
                }
            }.launch("register")
    }

    fun onDiscordClick() {
        view?.openDiscordInvite()
    }

    fun onEmailClick() {
        view?.openEmail(lastError?.message.ifNullOrBlank { "empty" })
    }

    private fun logRegisterEvent(
        studentsWithSemesters: List<StudentWithSemesters>,
        error: Throwable? = null
    ) {
        studentsWithSemesters.forEach { student ->
            analytics.logEvent(
                "registration_student_select",
                "success" to (error != null),
                "scrapperBaseUrl" to student.student.scrapperBaseUrl,
                "symbol" to student.student.symbol,
                "error" to (error?.message?.ifBlank { "No message" } ?: "No error")
            )
        }
    }
}
