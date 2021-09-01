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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

    private val dashboardItemLoadedList = mutableListOf<DashboardItem>()

    private val dashboardItemRefreshLoadedList = mutableListOf<DashboardItem>()

    private lateinit var dashboardItemsToLoad: Set<DashboardItem.Type>

    private lateinit var lastError: Throwable

    override fun onAttachView(view: DashboardView) {
        super.onAttachView(view)

        with(view) {
            initView()
            showProgress(true)
            showContent(false)
        }

        preferencesRepository.selectedDashboardTilesFlow
            .onEach { loadData(tilesToLoad = it) }
            .launch("dashboard_pref")
    }

    fun onDragAndDropEnd(list: List<DashboardItem>) {
        dashboardItemLoadedList.clear()
        dashboardItemLoadedList.addAll(list)

        val positionList =
            list.mapIndexed { index, dashboardItem -> Pair(dashboardItem.type, index) }.toMap()

        preferencesRepository.dashboardItemsPosition = positionList
    }

    fun loadData(forceRefresh: Boolean = false, tilesToLoad: Set<DashboardItem.Tile>) {
        dashboardItemsToLoad = tilesToLoad.map { it.toDashboardItemType() }.toSet()
        loadTiles(forceRefresh, dashboardItemsToLoad.toList())
    }

    private fun loadTiles(forceRefresh: Boolean, tileList: List<DashboardItem.Type>) {
        tileList.forEach {
            when (it) {
                DashboardItem.Type.ACCOUNT -> loadCurrentAccount(forceRefresh)
                DashboardItem.Type.HORIZONTAL_GROUP -> loadHorizontalGroup(forceRefresh)
                DashboardItem.Type.LESSONS -> loadLessons(forceRefresh)
                DashboardItem.Type.GRADES -> loadGrades(forceRefresh)
                DashboardItem.Type.HOMEWORK -> loadHomework(forceRefresh)
                DashboardItem.Type.ANNOUNCEMENTS -> loadSchoolAnnouncements(forceRefresh)
                DashboardItem.Type.EXAMS -> loadExams(forceRefresh)
                DashboardItem.Type.CONFERENCES -> loadConferences(forceRefresh)
                DashboardItem.Type.ADS -> TODO()
            }
        }
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the dashboard")
        loadData(true, preferencesRepository.selectedDashboardTiles)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true, preferencesRepository.selectedDashboardTiles)
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
        view?.showDashboardTileSettings(preferencesRepository.selectedDashboardTiles.toList())
        return true
    }

    fun onDashboardTileSettingSelected(selectedItems: List<String>) {
        preferencesRepository.selectedDashboardTiles = selectedItems.map {
            DashboardItem.Tile.valueOf(it)
        }.toSet()
    }

    private fun loadCurrentAccount(forceRefresh: Boolean) {
        flowWithResource { studentRepository.getCurrentStudent(false) }
            .onEach {
                when (it.status) {
                    Status.LOADING -> {
                        Timber.i("Loading dashboard account data started")
                        if (forceRefresh) return@onEach
                        updateData(DashboardItem.Account(it.data, isLoading = true), forceRefresh)
                    }
                    Status.SUCCESS -> {
                        Timber.i("Loading dashboard account result: Success")
                        updateData(DashboardItem.Account(it.data), forceRefresh)
                    }
                    Status.ERROR -> {
                        Timber.i("Loading dashboard account result: An exception occurred")
                        errorHandler.dispatch(it.error!!)
                        updateData(DashboardItem.Account(error = it.error), forceRefresh)
                    }
                }
            }
            .launch("dashboard_account")
    }

    private fun loadHorizontalGroup(forceRefresh: Boolean) {
        flow {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            val luckyNumberFlow = luckyNumberRepository.getLuckyNumber(student, forceRefresh)
            val messageFLow = messageRepository.getMessages(
                student = student,
                semester = semester,
                folder = MessageFolder.RECEIVED,
                forceRefresh = forceRefresh
            )
            val attendanceFlow = attendanceSummaryRepository.getAttendanceSummary(
                student = student,
                semester = semester,
                subjectId = -1,
                forceRefresh = forceRefresh
            )

            emitAll(combine(
                if (DashboardItem.Tile.LUCKY_NUMBER in preferencesRepository.selectedDashboardTiles)
                    luckyNumberFlow else flowOf(null),
                if (DashboardItem.Tile.MESSAGES in preferencesRepository.selectedDashboardTiles)
                    messageFLow else flowOf(null),
                if (DashboardItem.Tile.ATTENDANCE in preferencesRepository.selectedDashboardTiles)
                    attendanceFlow else flowOf(null)
            ) { luckyNumberResource, messageResource, attendanceResource ->
                val error =
                    luckyNumberResource?.error ?: messageResource?.error ?: attendanceResource?.error
                error?.let { throw it }

                val luckyNumber = luckyNumberResource?.data?.luckyNumber
                val messageCount = messageResource?.data?.count { it.unread }
                val attendancePercentage = attendanceResource?.data?.calculatePercentage()

                DashboardItem.HorizontalGroup(
                    isLoading = (luckyNumberResource?.status == Status.LOADING || messageResource?.status == Status.LOADING || attendanceResource?.status == Status.LOADING),
                    attendancePercentage = attendancePercentage,
                    unreadMessagesCount = messageCount,
                    luckyNumber = luckyNumber
                )
            })
        }
            .filterNot { it.isLoading && forceRefresh }
            .distinctUntilChanged()
            .onEach {
                updateData(it, forceRefresh)
            }
            .catch {
                updateData(DashboardItem.HorizontalGroup(error = it), forceRefresh)
                errorHandler.dispatch(it)
            }
            .launch("horizontal")
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
                        DashboardItem.Grades(
                            subjectWithGrades = it.data,
                            gradeTheme = preferencesRepository.gradeColorTheme,
                            isLoading = true
                        ), forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard grades result: Success")
                    updateData(
                        DashboardItem.Grades(
                            subjectWithGrades = it.data,
                            gradeTheme = preferencesRepository.gradeColorTheme
                        ), forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard grades result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Grades(error = it.error), forceRefresh)
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
                    updateData(DashboardItem.Lessons(it.data, isLoading = true), forceRefresh)
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard lessons result: Success")
                    updateData(DashboardItem.Lessons(it.data), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard lessons result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Lessons(error = it.error), forceRefresh)
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
                        DashboardItem.Homework(it.data ?: emptyList(), isLoading = true),
                        forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard homework result: Success")
                    updateData(DashboardItem.Homework(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard homework result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Homework(error = it.error), forceRefresh)
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
                        DashboardItem.Announcements(
                            it.data ?: emptyList(),
                            isLoading = true
                        ), forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard announcements result: Success")
                    updateData(DashboardItem.Announcements(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard announcements result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Announcements(error = it.error), forceRefresh)
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
                        DashboardItem.Exams(it.data.orEmpty(), isLoading = true),
                        forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard exams result: Success")
                    updateData(DashboardItem.Exams(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard exams result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Exams(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_exams")
    }

    private fun loadConferences(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            conferenceRepository.getConferences(
                student = student,
                semester = semester,
                forceRefresh = forceRefresh,
                startDate = LocalDateTime.now()
            )
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard conferences data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardItem.Conferences(it.data ?: emptyList(), isLoading = true),
                        forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard conferences result: Success")
                    updateData(DashboardItem.Conferences(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard conferences result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Conferences(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_conferences")
    }

    private fun updateData(dashboardItem: DashboardItem, forceRefresh: Boolean) {
        val isForceRefreshError = forceRefresh && dashboardItem.error != null
        val dashboardItemsPosition = preferencesRepository.dashboardItemsPosition

        with(dashboardItemLoadedList) {
            removeAll { it.type == dashboardItem.type && !isForceRefreshError }
            if (!isForceRefreshError) add(dashboardItem)
            sortBy { tile -> dashboardItemsToLoad.single { it == tile.type }.ordinal }
        }

        if (forceRefresh) {
            with(dashboardItemRefreshLoadedList) {
                removeAll { it.type == dashboardItem.type }
                add(dashboardItem)
            }
        }

        dashboardItemLoadedList.sortBy { tile ->
            dashboardItemsPosition?.getOrDefault(
                tile.type,
                tile.type.ordinal + 100
            ) ?: tile.type.ordinal
        }

        val isItemsLoaded =
            dashboardItemsToLoad.all { type -> dashboardItemLoadedList.any { it.type == type } }
        val isRefreshItemLoaded =
            dashboardItemsToLoad.all { type -> dashboardItemRefreshLoadedList.any { it.type == type } }
        val isItemsDataLoaded = isItemsLoaded && dashboardItemLoadedList.all {
            it.isDataLoaded || it.error != null
        }
        val isRefreshItemsDataLoaded = isRefreshItemLoaded && dashboardItemRefreshLoadedList.all {
            it.isDataLoaded || it.error != null
        }

        if (isRefreshItemsDataLoaded) {
            view?.showRefresh(false)
            dashboardItemRefreshLoadedList.clear()
        }

        view?.run {
            if (!forceRefresh) {
                showProgress(!isItemsDataLoaded)
                showContent(isItemsDataLoaded)
            }
            updateData(dashboardItemLoadedList.toList())
        }

        if (isItemsLoaded) {
            val filteredItems =
                dashboardItemLoadedList.filterNot { it.type == DashboardItem.Type.ACCOUNT }
            val isAccountItemError =
                dashboardItemLoadedList.single { it.type == DashboardItem.Type.ACCOUNT }.error != null
            val isGeneralError =
                filteredItems.all { it.error != null } && filteredItems.isNotEmpty() || isAccountItemError

            val errorMessage = filteredItems.map { it.error?.stackTraceToString() }.toString()

            lastError = Exception(errorMessage)

            view?.run {
                showProgress(false)
                showContent(!isGeneralError)
                showErrorView(isGeneralError)
            }
        }
    }
}