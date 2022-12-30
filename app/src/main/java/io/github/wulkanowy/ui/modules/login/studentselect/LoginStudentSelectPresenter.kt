package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.mappers.mapToStudentWithSemesters
import io.github.wulkanowy.data.pojos.RegisterStudent
import io.github.wulkanowy.data.pojos.RegisterSymbol
import io.github.wulkanowy.data.pojos.RegisterUnit
import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.sdk.scrapper.login.AccountPermissionException
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginData
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
    private lateinit var loginData: LoginData

    private lateinit var students: List<StudentWithSemesters>
    private var isEmptySymbolsExpanded = false
    private var expandedSymbolError: RegisterSymbol? = null
    private var expandedSchoolError: RegisterUnit? = null

    private val selectedSubjects = mutableListOf<LoginStudentSelectItem.Student>()

    fun onAttachView(
        view: LoginStudentSelectView,
        loginData: LoginData,
        registerUser: RegisterUser,
    ) {
        super.onAttachView(view)
        with(view) {
            initView()
            enableSignIn(false)
            loginErrorHandler.onStudentDuplicate = {
                showMessage(it)
                Timber.i("The student already registered in the app was selected")
            }
        }

        this.loginData = loginData
        this.registerUser = registerUser
        loadData()
    }

    private fun loadData() {
        resetSelectedState()

        resourceFlow { studentRepository.getSavedStudents(false) }.onEach {
            students = it.dataOrNull.orEmpty()
            when (it) {
                is Resource.Loading -> Timber.d("Login student select students load started")
                is Resource.Success -> refreshItems()
                is Resource.Error -> {
                    errorHandler.dispatch(it.error)
                    lastError = it.error
                    refreshItems()
                }
            }
        }.launch()
    }

    private fun createItems(): List<LoginStudentSelectItem> = buildList {
        val notEmptySymbols = registerUser.symbols.filter { it.schools.isNotEmpty() }
        val emptySymbols = registerUser.symbols.filter { it.schools.isEmpty() }

        if (emptySymbols.isNotEmpty() && notEmptySymbols.isNotEmpty() && emptySymbols.any { it.symbol == loginData.symbol }) {
            add(createEmptySymbolItem(emptySymbols.first { it.symbol == loginData.symbol }))
        }

        addAll(createNotEmptySymbolItems(notEmptySymbols, students))
        addAll(createEmptySymbolItems(emptySymbols, notEmptySymbols.isNotEmpty()))

        val helpItem = LoginStudentSelectItem.Help(
            onEnterSymbolClick = ::onEnterSymbol,
            onContactUsClick = ::onEmailClick,
            onDiscordClick = ::onDiscordClick,
        )
        add(helpItem)
    }

    private fun createNotEmptySymbolItems(
        notEmptySymbols: List<RegisterSymbol>,
        students: List<StudentWithSemesters>,
    ) = buildList {
        notEmptySymbols.forEach { registerSymbol ->
            val symbolHeader = LoginStudentSelectItem.SymbolHeader(
                symbol = registerSymbol,
                humanReadableName = view?.symbols?.get(registerSymbol.symbol),
                isErrorExpanded = expandedSymbolError == registerSymbol,
                onClick = ::onSymbolItemClick,
            )
            add(symbolHeader)

            registerSymbol.schools.forEach { registerUnit ->
                val schoolHeader = LoginStudentSelectItem.SchoolHeader(
                    unit = registerUnit,
                    isErrorExpanded = expandedSchoolError == registerUnit,
                    onClick = ::onUnitItemClick,
                )
                add(schoolHeader)

                registerUnit.subjects.filterIsInstance<RegisterStudent>().forEach { subject ->
                    add(createStudentItem(subject, registerSymbol, registerUnit, students))
                }
            }
        }
    }

    private fun createStudentItem(
        student: RegisterStudent,
        symbol: RegisterSymbol,
        school: RegisterUnit,
        students: List<StudentWithSemesters>,
    ) = LoginStudentSelectItem.Student(
        symbol = symbol,
        unit = school,
        student = student,
        onClick = ::onItemSelected,
        isEnabled = students.none {
            it.student.email == registerUser.login
                && it.student.symbol == symbol.symbol
                && it.student.studentId == student.studentId
                && it.student.schoolSymbol == school.schoolId
                && it.student.classId == student.classId
        },
        isSelected = student in selectedSubjects.map { it.student },
    )

    private fun createEmptySymbolItems(
        emptySymbols: List<RegisterSymbol>,
        isNotEmptySymbolsExist: Boolean,
    ) = buildList {
        val filteredEmptySymbols = emptySymbols.filter {
            it.error !is AccountPermissionException
        }.ifEmpty { emptySymbols.takeIf { !isNotEmptySymbolsExist }.orEmpty() }

        if (filteredEmptySymbols.isNotEmpty() && isNotEmptySymbolsExist) {
            val emptyHeader = LoginStudentSelectItem.EmptySymbolsHeader(
                isExpanded = isEmptySymbolsExpanded,
                onClick = ::onEmptySymbolsToggle,
            )
            add(emptyHeader)
            if (isEmptySymbolsExpanded) {
                filteredEmptySymbols.forEach {
                    add(createEmptySymbolItem(it))
                }
            }
        }

        if (filteredEmptySymbols.isNotEmpty() && !isNotEmptySymbolsExist) {
            filteredEmptySymbols.forEach {
                add(createEmptySymbolItem(it))
            }
        }
    }

    private fun createEmptySymbolItem(registerSymbol: RegisterSymbol) =
        LoginStudentSelectItem.SymbolHeader(
            symbol = registerSymbol,
            humanReadableName = view?.symbols?.get(registerSymbol.symbol),
            isErrorExpanded = expandedSymbolError == registerSymbol,
            onClick = ::onSymbolItemClick,
        )

    fun onSignIn() {
        registerStudents(selectedSubjects)
    }

    private fun onEmptySymbolsToggle() {
        isEmptySymbolsExpanded = !isEmptySymbolsExpanded

        refreshItems()
    }

    private fun onItemSelected(item: LoginStudentSelectItem.Student) {
        if (!item.isEnabled) return

        selectedSubjects
            .removeAll { it.student == item.student }
            .let { if (!it) selectedSubjects.add(item) }

        view?.enableSignIn(selectedSubjects.isNotEmpty())
        refreshItems()
    }

    private fun onSymbolItemClick(symbol: RegisterSymbol) {
        expandedSymbolError = if (symbol != expandedSymbolError) symbol else null
        refreshItems()
    }

    private fun onUnitItemClick(unit: RegisterUnit) {
        expandedSchoolError = if (unit != expandedSchoolError) unit else null
        refreshItems()
    }

    private fun resetSelectedState() {
        selectedSubjects.clear()
        view?.enableSignIn(false)
    }

    private fun refreshItems() {
        view?.updateData(createItems())
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
                        }
                        lastError = it.error
                        loginErrorHandler.dispatch(it.error)
                        logRegisterEvent(studentsWithSemesters, it.error)
                    }
                }
            }.launch("register")
    }

    private fun onEnterSymbol() {
        view?.navigateToSymbol(loginData)
    }

    private fun onDiscordClick() {
        view?.openDiscordInvite()
    }

    private fun onEmailClick() {
        view?.openEmail(lastError?.message.ifNullOrBlank {
            registerUser.symbols.flatMap { symbol ->
                symbol.schools.map { it.error?.message } + symbol.error?.message
            }.filterNotNull().distinct().joinToString("; ") {
                it.take(46) + "..."
            }.ifEmpty { "blank" }
        })
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
