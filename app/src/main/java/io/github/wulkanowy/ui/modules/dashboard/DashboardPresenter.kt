package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.ConferenceRepository
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SchoolAnnouncementRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.calculatePercentage
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class DashboardPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val luckyNumberRepository: LuckyNumberRepository,
    private val gradeRepository: GradeRepository,
    private val semesterRepository: SemesterRepository,
    private val messageRepository: MessageRepository,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val timetableRepository: TimetableRepository,
    private val homeworkRepository: HomeworkRepository,
    private val examRepository: ExamRepository,
    private val conferenceRepository: ConferenceRepository,
    private val preferencesRepository: PreferencesRepository,
    private val schoolAnnouncementRepository: SchoolAnnouncementRepository
) : BasePresenter<DashboardView>(errorHandler, studentRepository) {

    private val dashboardTileLoadedList = mutableListOf<DashboardTile>()

    private val dashboardTileRefreshLoadedList = mutableListOf<DashboardTile>()

    private lateinit var dashboardTilesToLoad: Set<DashboardTile.Type>

    private var dashboardDataToLoad: Set<DashboardTile.DataType>? = null

    private lateinit var lastError: Throwable

    override fun onAttachView(view: DashboardView) {
        super.onAttachView(view)

        with(view) {
            initView()
            showProgress(true)
            showContent(false)
        }

        preferencesRepository.dashboardDataFlow
            .onEach { loadData(dataToLoad = it) }
            .launch("dashboard_pref")
    }

    fun loadData(forceRefresh: Boolean = false, dataToLoad: Set<DashboardTile.DataType>) {
        val oldDashboardDataToLoad = dashboardDataToLoad.orEmpty()

        dashboardDataToLoad = dataToLoad
        dashboardTilesToLoad = dashboardDataToLoad.orEmpty().map { it.toDashboardType() }.toSet()

        removeUnselectedTiles()
        loadSelectedTiles(forceRefresh, oldDashboardDataToLoad)
    }

    private fun removeUnselectedTiles() {
        val isLuckyNumberToLoad =
            dashboardDataToLoad.orEmpty().any { it == DashboardTile.DataType.LUCKY_NUMBER }
        val isMessagesToLoad =
            dashboardDataToLoad.orEmpty().any { it == DashboardTile.DataType.MESSAGES }
        val isAttendanceToLoad =
            dashboardDataToLoad.orEmpty().any { it == DashboardTile.DataType.ATTENDANCE }

        dashboardTileLoadedList.removeAll { loadedTile -> dashboardTilesToLoad.none { it == loadedTile.type } }

        val horizontalGroup =
            dashboardTileLoadedList.find { it is DashboardTile.HorizontalGroup } as DashboardTile.HorizontalGroup?

        if (horizontalGroup != null) {
            val horizontalIndex = dashboardTileLoadedList.indexOf(horizontalGroup)
            dashboardTileLoadedList.remove(horizontalGroup)

            var updatedHorizontalGroup = horizontalGroup

            if (horizontalGroup.luckyNumber != null && !isLuckyNumberToLoad) {
                updatedHorizontalGroup = updatedHorizontalGroup.copy(luckyNumber = null)
            }

            if (horizontalGroup.attendancePercentage != null && !isAttendanceToLoad) {
                updatedHorizontalGroup = updatedHorizontalGroup.copy(attendancePercentage = null)
            }

            if (horizontalGroup.unreadMessagesCount != null && !isMessagesToLoad) {
                updatedHorizontalGroup = updatedHorizontalGroup.copy(unreadMessagesCount = null)
            }

            if (horizontalGroup.error != null) {
                updatedHorizontalGroup = updatedHorizontalGroup.copy(error = null, isLoading = true)
            }

            dashboardTileLoadedList.add(horizontalIndex, updatedHorizontalGroup)
        }

        view?.updateData(dashboardTileLoadedList)
    }

    private fun loadSelectedTiles(
        forceRefresh: Boolean,
        oldDashboardDataToLoad: Set<DashboardTile.DataType>
    ) {
        dashboardDataToLoad.orEmpty()
            .filter { newDataTypeToLoad -> oldDashboardDataToLoad.none { it == newDataTypeToLoad } || forceRefresh }
            .forEach {
                when (it) {
                    DashboardTile.DataType.ACCOUNT -> loadCurrentAccount(forceRefresh)
                    DashboardTile.DataType.LUCKY_NUMBER -> loadLuckyNumber(forceRefresh)
                    DashboardTile.DataType.MESSAGES -> loadMessages(forceRefresh)
                    DashboardTile.DataType.ATTENDANCE -> loadAttendance(forceRefresh)
                    DashboardTile.DataType.LESSONS -> loadLessons(forceRefresh)
                    DashboardTile.DataType.GRADES -> loadGrades(forceRefresh)
                    DashboardTile.DataType.HOMEWORK -> loadHomework(forceRefresh)
                    DashboardTile.DataType.ANNOUNCEMENTS -> loadSchoolAnnouncements(forceRefresh)
                    DashboardTile.DataType.EXAMS -> loadExams(forceRefresh)
                    DashboardTile.DataType.CONFERENCES -> loadConferences(forceRefresh)
                    DashboardTile.DataType.ADS -> TODO()
                }
            }
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the dashboard")
        loadData(true, preferencesRepository.dashboardData)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true, preferencesRepository.dashboardData)
    }

    fun onViewReselected() {
        Timber.i("Dashboard view is reselected")
        view?.run {
            resetView()
            popViewToRoot()
        }
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onDashboardTileSettingsSelected(): Boolean {
        view?.showDashboardTileSettings(preferencesRepository.dashboardData.toList())
        return true
    }

    fun onDashboardTileSettingSelected(selectedItems: List<String>) {
        preferencesRepository.dashboardData = selectedItems.map {
            DashboardTile.DataType.valueOf(it)
        }.toSet()
    }

    private fun loadCurrentAccount(forceRefresh: Boolean) {
        flowWithResource { studentRepository.getCurrentStudent(false) }
            .onEach {
                when (it.status) {
                    Status.LOADING -> {
                        Timber.i("Loading dashboard account data started")
                        if (forceRefresh) return@onEach
                        updateData(DashboardTile.Account(it.data, isLoading = true), forceRefresh)
                    }
                    Status.SUCCESS -> {
                        Timber.i("Loading dashboard account result: Success")
                        updateData(DashboardTile.Account(it.data), forceRefresh)
                    }
                    Status.ERROR -> {
                        Timber.i("Loading dashboard account result: An exception occurred")
                        errorHandler.dispatch(it.error!!)
                        updateData(DashboardTile.Account(error = it.error), forceRefresh)
                    }
                }
            }
            .launch("dashboard_account")
    }

    private fun loadLuckyNumber(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)

            luckyNumberRepository.getLuckyNumber(student, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard lucky number data started")
                    if (forceRefresh) return@onEach
                    processHorizontalGroupData(
                        luckyNumber = it.data?.luckyNumber,
                        isLoading = true,
                        forceRefresh = forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard lucky number result: Success")
                    processHorizontalGroupData(
                        luckyNumber = it.data?.luckyNumber ?: -1,
                        forceRefresh = forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard lucky number result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    processHorizontalGroupData(error = it.error, forceRefresh = forceRefresh)
                }
            }
        }.launch("dashboard_lucky_number")
    }

    private fun loadMessages(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            messageRepository.getMessages(student, semester, MessageFolder.RECEIVED, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard messages data started")
                    if (forceRefresh) return@onEach
                    val unreadMessagesCount = it.data?.count { message -> message.unread }

                    processHorizontalGroupData(
                        unreadMessagesCount = unreadMessagesCount,
                        isLoading = true,
                        forceRefresh = forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard messages result: Success")
                    val unreadMessagesCount = it.data?.count { message -> message.unread }

                    processHorizontalGroupData(
                        unreadMessagesCount = unreadMessagesCount,
                        forceRefresh = forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard messages result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    processHorizontalGroupData(error = it.error, forceRefresh = forceRefresh)
                }
            }
        }.launch("dashboard_messages")
    }

    private fun loadAttendance(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            attendanceSummaryRepository.getAttendanceSummary(student, semester, -1, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard attendance data started")
                    if (forceRefresh) return@onEach
                    val attendancePercentage = it.data?.calculatePercentage()

                    processHorizontalGroupData(
                        attendancePercentage = attendancePercentage,
                        isLoading = true,
                        forceRefresh = forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard attendance result: Success")
                    val attendancePercentage = it.data?.calculatePercentage()

                    processHorizontalGroupData(
                        attendancePercentage = attendancePercentage,
                        forceRefresh = forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard attendance result: An exception occurred")
                    errorHandler.dispatch(it.error!!)

                    processHorizontalGroupData(error = it.error, forceRefresh = forceRefresh)
                }
            }
        }.launch("dashboard_attendance")
    }

    private fun loadGrades(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            gradeRepository.getGrades(student, semester, forceRefresh)
        }.map { originalResource ->
            val filteredSubjectWithGrades = originalResource.data?.first.orEmpty()
                .filter { grade ->
                    grade.date.isAfter(LocalDate.now().minusDays(7))
                }
                .groupBy { grade -> grade.subject }
                .mapValues { entry ->
                    entry.value
                        .take(5)
                        .sortedBy { grade -> grade.date }
                }
                .toList()
                .sortedBy { subjectWithGrades -> subjectWithGrades.second[0].date }
                .toMap()

            Resource(
                status = originalResource.status,
                data = filteredSubjectWithGrades.takeIf { originalResource.data != null },
                error = originalResource.error
            )
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard grades data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardTile.Grades(
                            subjectWithGrades = it.data,
                            gradeTheme = preferencesRepository.gradeColorTheme,
                            isLoading = true
                        ), forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard grades result: Success")
                    updateData(
                        DashboardTile.Grades(
                            subjectWithGrades = it.data,
                            gradeTheme = preferencesRepository.gradeColorTheme
                        ), forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard grades result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardTile.Grades(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_grades")
    }

    private fun loadLessons(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)
            val date = LocalDate.now().nextOrSameSchoolDay

            timetableRepository.getTimetable(
                student = student,
                semester = semester,
                start = date,
                end = date.plusDays(1),
                forceRefresh = forceRefresh
            )

        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard lessons data started")
                    if (forceRefresh) return@onEach
                    updateData(DashboardTile.Lessons(it.data, isLoading = true), forceRefresh)
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard lessons result: Success")
                    updateData(DashboardTile.Lessons(it.data), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard lessons result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardTile.Lessons(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_lessons")
    }

    private fun loadHomework(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)
            val date = LocalDate.now().nextOrSameSchoolDay

            homeworkRepository.getHomework(
                student = student,
                semester = semester,
                start = date,
                end = date,
                forceRefresh = forceRefresh
            )
        }.map { homeworkResource ->
            val currentDate = LocalDate.now()

            val filteredHomework = homeworkResource.data?.filter {
                (it.date.isAfter(currentDate) || it.date == currentDate) && !it.isDone
            }

            homeworkResource.copy(data = filteredHomework)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard homework data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardTile.Homework(it.data ?: emptyList(), isLoading = true),
                        forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard homework result: Success")
                    updateData(DashboardTile.Homework(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard homework result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardTile.Homework(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_homework")
    }

    private fun loadSchoolAnnouncements(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)

            schoolAnnouncementRepository.getSchoolAnnouncements(student, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard announcements data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardTile.Announcements(
                            it.data ?: emptyList(),
                            isLoading = true
                        ), forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard announcements result: Success")
                    updateData(DashboardTile.Announcements(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard announcements result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardTile.Announcements(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_announcements")
    }

    private fun loadExams(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            examRepository.getExams(
                student = student,
                semester = semester,
                start = LocalDate.now(),
                end = LocalDate.now().plusDays(7),
                forceRefresh = forceRefresh
            )
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard exams data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardTile.Exams(it.data ?: emptyList(), isLoading = true),
                        forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard exams result: Success")
                    updateData(DashboardTile.Exams(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard exams result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardTile.Exams(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_exams")
    }

    private fun loadConferences(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            conferenceRepository.getConferences(student, semester, forceRefresh)
        }.map { conferencesResource ->
            val currentDateTime = LocalDateTime.now()

            val filteredConferences = conferencesResource.data?.filter {
                it.date.isAfter(currentDateTime)
            }

            conferencesResource.copy(data = filteredConferences)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard conferences data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardTile.Conferences(it.data ?: emptyList(), isLoading = true),
                        forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard conferences result: Success")
                    updateData(DashboardTile.Conferences(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard conferences result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardTile.Conferences(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_conferences")
    }

    private fun processHorizontalGroupData(
        luckyNumber: Int? = null,
        unreadMessagesCount: Int? = null,
        attendancePercentage: Double? = null,
        error: Throwable? = null,
        isLoading: Boolean = false,
        forceRefresh: Boolean
    ) {
        val isLuckyNumberToLoad =
            dashboardDataToLoad.orEmpty().any { it == DashboardTile.DataType.LUCKY_NUMBER }
        val isMessagesToLoad =
            dashboardDataToLoad.orEmpty().any { it == DashboardTile.DataType.MESSAGES }
        val isAttendanceToLoad =
            dashboardDataToLoad.orEmpty().any { it == DashboardTile.DataType.ATTENDANCE }
        val isPushedToList =
            dashboardTileLoadedList.any { it.type == DashboardTile.Type.HORIZONTAL_GROUP }

        if (error != null) {
            updateData(DashboardTile.HorizontalGroup(error = error), forceRefresh)
            return
        }

        if (isLoading) {
            val horizontalGroup =
                dashboardTileLoadedList.find { it is DashboardTile.HorizontalGroup } as DashboardTile.HorizontalGroup?
            val updatedHorizontalGroup =
                horizontalGroup?.copy(isLoading = true) ?: DashboardTile.HorizontalGroup(isLoading = true)

            updateData(updatedHorizontalGroup, forceRefresh)
        }

        if (forceRefresh && !isPushedToList) {
            updateData(DashboardTile.HorizontalGroup(), forceRefresh)
        }

        val horizontalGroup =
            dashboardTileLoadedList.single { it is DashboardTile.HorizontalGroup } as DashboardTile.HorizontalGroup

        when {
            luckyNumber != null -> {
                updateData(horizontalGroup.copy(luckyNumber = luckyNumber), forceRefresh)
            }
            unreadMessagesCount != null -> {
                updateData(
                    horizontalGroup.copy(unreadMessagesCount = unreadMessagesCount),
                    forceRefresh
                )
            }
            attendancePercentage != null -> {
                updateData(
                    horizontalGroup.copy(attendancePercentage = attendancePercentage),
                    forceRefresh
                )
            }
        }

        val isHorizontalGroupLoaded = dashboardTileLoadedList.any {
            if (it !is DashboardTile.HorizontalGroup) return@any false

            val isLuckyNumberStateCorrect = (it.luckyNumber != null) == isLuckyNumberToLoad
            val isMessagesStateCorrect = (it.unreadMessagesCount != null) == isMessagesToLoad
            val isAttendanceStateCorrect = (it.attendancePercentage != null) == isAttendanceToLoad

            isLuckyNumberStateCorrect && isAttendanceStateCorrect && isMessagesStateCorrect
        }

        if (isHorizontalGroupLoaded) {
            val updatedHorizontalGroup =
                dashboardTileLoadedList.single { it is DashboardTile.HorizontalGroup } as DashboardTile.HorizontalGroup

            updateData(updatedHorizontalGroup.copy(isLoading = false, error = null), forceRefresh)
        }
    }

    private fun updateData(dashboardTile: DashboardTile, forceRefresh: Boolean) {
        val isForceRefreshError = forceRefresh && dashboardTile.error != null

        with(dashboardTileLoadedList) {
            removeAll { it.type == dashboardTile.type && !isForceRefreshError }
            if (!isForceRefreshError) add(dashboardTile)
            sortBy { tile -> dashboardTilesToLoad.single { it == tile.type }.ordinal }
        }

        if (forceRefresh) {
            with(dashboardTileRefreshLoadedList) {
                removeAll { it.type == dashboardTile.type }
                add(dashboardTile)
            }
        }

        dashboardTileLoadedList.sortBy { tile -> dashboardTilesToLoad.single { it == tile.type }.ordinal }

        val isTilesLoaded =
            dashboardTilesToLoad.all { type -> dashboardTileLoadedList.any { it.type == type } }
        val isRefreshTileLoaded =
            dashboardTilesToLoad.all { type -> dashboardTileRefreshLoadedList.any { it.type == type } }
        val isTilesDataLoaded = isTilesLoaded && dashboardTileLoadedList.all {
            it.isDataLoaded || it.error != null
        }
        val isRefreshTilesDataLoaded = isRefreshTileLoaded && dashboardTileRefreshLoadedList.all {
            it.isDataLoaded || it.error != null
        }

        if (isRefreshTilesDataLoaded) {
            view?.showRefresh(false)
            dashboardTileRefreshLoadedList.clear()
        }

        view?.run {
            if (!forceRefresh) {
                showProgress(!isTilesDataLoaded)
                showContent(isTilesDataLoaded)
            }
            updateData(dashboardTileLoadedList.toList())
        }

        if (isTilesLoaded) {
            val filteredTiles =
                dashboardTileLoadedList.filterNot { it.type == DashboardTile.Type.ACCOUNT }
            val isAccountTileError =
                dashboardTileLoadedList.single { it.type == DashboardTile.Type.ACCOUNT }.error != null
            val isGeneralError =
                filteredTiles.all { it.error != null } && filteredTiles.isNotEmpty() || isAccountTileError

            val errorMessage = filteredTiles.map { it.error?.stackTraceToString() }.toString()

            lastError = Exception(errorMessage)

            view?.run {
                showProgress(false)
                showContent(!isGeneralError)
                showErrorView(isGeneralError)
            }
        }
    }
}