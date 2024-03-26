package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.enums.MessageType
import io.github.wulkanowy.data.errorOrNull
import io.github.wulkanowy.data.flatResourceFlow
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.mapResourceData
import io.github.wulkanowy.data.onResourceError
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
import io.github.wulkanowy.domain.adminmessage.GetAppropriateAdminMessageUseCase
import io.github.wulkanowy.domain.timetable.IsStudentHasLessonsOnWeekendUseCase
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.dashboard.DashboardItem.Importance.NonBlocking
import io.github.wulkanowy.utils.AdsHelper
import io.github.wulkanowy.utils.EnumMap
import io.github.wulkanowy.utils.calculatePercentage
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.toEnumSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.util.EnumSet
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
    private val isStudentHasLessonsOnWeekendUseCase: IsStudentHasLessonsOnWeekendUseCase,
    private val homeworkRepository: HomeworkRepository,
    private val examRepository: ExamRepository,
    private val conferenceRepository: ConferenceRepository,
    private val preferencesRepository: PreferencesRepository,
    private val schoolAnnouncementRepository: SchoolAnnouncementRepository,
    private val getAppropriateAdminMessageUseCase: GetAppropriateAdminMessageUseCase,
    private val adsHelper: AdsHelper
) : BasePresenter<DashboardView>(errorHandler, studentRepository) {

    private val dashboardItemLoadedList = EnumMap<DashboardItem.Type, DashboardItem>()

    private val dashboardItemRefreshLoadedList = EnumMap<DashboardItem.Type, DashboardItem>()

    private var dashboardItemsToLoad = emptySet<DashboardItem.Type>()

    private var dashboardTileLoadedList = emptySet<DashboardItem.Tile>()

    // List of types that have loaded actual data at least once
    private val firstLoadedItemList = EnumSet.noneOf(DashboardItem.Type::class.java)

    private val selectedDashboardTiles
        get() = preferencesRepository.selectedDashboardTiles

    private lateinit var lastError: Throwable

    override fun onAttachView(view: DashboardView) {
        super.onAttachView(view)

        with(view) {
            initView()
            showProgress(true)
            showContent(false)
        }

        preferencesRepository.selectedDashboardTilesFlow.onEach {
            loadData(tilesToLoad = it)
        }.launch("dashboard_pref")
    }

    fun onAdminMessageDismissed(adminMessage: AdminMessage) {
        preferencesRepository.dismissedAdminMessageIds += adminMessage.id

        loadData(selectedDashboardTiles)
    }

    fun onDragAndDropEnd(list: List<DashboardItem>) {
        with(dashboardItemLoadedList) {
            clear()
            for (item in list) {
                put(item.type, item)
            }
        }

        val positionList =
            list.mapIndexed { index, dashboardItem -> Pair(dashboardItem.type, index) }.toMap()

        preferencesRepository.dashboardItemsPosition = positionList
    }

    fun loadData(
        tilesToLoad: Set<DashboardItem.Tile>,
        forceRefresh: Boolean = false,
    ) {
        val oldDashboardTileLoadedList = dashboardTileLoadedList
        dashboardItemsToLoad = tilesToLoad.map(DashboardItem.Tile::type).toEnumSet()
        dashboardTileLoadedList = tilesToLoad

        val itemsToLoad = generateDashboardTileListToLoad(
            dashboardTilesToLoad = tilesToLoad,
            dashboardLoadedTiles = oldDashboardTileLoadedList,
            forceRefresh = forceRefresh
        ).map(DashboardItem.Tile::type)

        removeUnselectedTiles(tilesToLoad)
        loadTiles(tileList = itemsToLoad, forceRefresh = forceRefresh)
    }

    private fun generateDashboardTileListToLoad(
        dashboardTilesToLoad: Set<DashboardItem.Tile>,
        dashboardLoadedTiles: Set<DashboardItem.Tile>,
        forceRefresh: Boolean
    ) = dashboardTilesToLoad.filter { newItemToLoad ->
        forceRefresh || newItemToLoad.type.refreshBehavior == DashboardItem.RefreshBehavior.Always || !dashboardLoadedTiles.contains(
            newItemToLoad
        )
    }

    private fun removeUnselectedTiles(tilesToLoad: Collection<DashboardItem.Tile>) {
        dashboardItemLoadedList.values.retainAll { loadedTile ->
            dashboardItemsToLoad.contains(loadedTile.type)
        }

        val horizontalGroup =
            dashboardItemLoadedList[DashboardItem.Type.HORIZONTAL_GROUP] as DashboardItem.HorizontalGroup?
        if (horizontalGroup != null) {
            val isAttendanceToLoad = DashboardItem.Tile.ATTENDANCE in tilesToLoad
            val isMessagesToLoad = DashboardItem.Tile.MESSAGES in tilesToLoad
            val isLuckyNumberToLoad = DashboardItem.Tile.LUCKY_NUMBER in tilesToLoad

            val newHorizontalGroup = horizontalGroup.copy(
                attendancePercentage = horizontalGroup.attendancePercentage.takeIf { isAttendanceToLoad },
                unreadMessagesCount = horizontalGroup.unreadMessagesCount.takeIf { isMessagesToLoad },
                luckyNumber = horizontalGroup.luckyNumber.takeIf { isLuckyNumberToLoad }
            )

            dashboardItemLoadedList[DashboardItem.Type.HORIZONTAL_GROUP] = newHorizontalGroup
        }

        view?.updateData()
    }

    private fun loadTiles(
        tileList: List<DashboardItem.Type>,
        forceRefresh: Boolean
    ) {
        presenterScope.launch {
            Timber.i("Loading dashboard account data started")
            val student = runCatching { studentRepository.getCurrentStudent(true) }
                .onFailure {
                    Timber.i("Loading dashboard account result: An exception occurred")
                    errorHandler.dispatch(it)
                    updateData(DashboardItem.Account(error = it), forceRefresh)
                }
                .onSuccess { Timber.i("Loading dashboard account result: Success") }
                .getOrNull() ?: return@launch

            tileList.forEach {
                when (it) {
                    DashboardItem.Type.ACCOUNT -> {
                        updateData(DashboardItem.Account(student), forceRefresh)
                    }

                    DashboardItem.Type.HORIZONTAL_GROUP -> {
                        loadHorizontalGroup(student, forceRefresh)
                    }

                    DashboardItem.Type.LESSONS -> loadLessons(student, forceRefresh)
                    DashboardItem.Type.GRADES -> loadGrades(student, forceRefresh)
                    DashboardItem.Type.HOMEWORK -> loadHomework(student, forceRefresh)
                    DashboardItem.Type.ANNOUNCEMENTS -> {
                        loadSchoolAnnouncements(student, forceRefresh)
                    }

                    DashboardItem.Type.EXAMS -> loadExams(student, forceRefresh)
                    DashboardItem.Type.CONFERENCES -> {
                        loadConferences(student, forceRefresh)
                    }

                    DashboardItem.Type.ADS -> loadAds(forceRefresh)
                    DashboardItem.Type.ADMIN_MESSAGE -> loadAdminMessage(student, forceRefresh)
                }
            }
        }
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the dashboard")
        loadData(selectedDashboardTiles, forceRefresh = true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(selectedDashboardTiles, forceRefresh = true)
    }

    fun onRetryAfterCaptcha() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(selectedDashboardTiles, forceRefresh = true)
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

    fun onNotificationsCenterSelected(): Boolean {
        view?.openNotificationsCenterView()
        return true
    }

    fun onDashboardTileSettingsSelected(): Boolean {
        view?.showDashboardTileSettings(selectedDashboardTiles)
        return true
    }

    fun onDashboardTileSettingSelected(selectedItems: List<String>) {
        preferencesRepository.selectedDashboardTiles = selectedItems.map {
            DashboardItem.Tile.valueOf(it)
        }.toSet()
    }

    fun onAdminMessageSelected(url: String?) {
        url?.let { view?.openInternetBrowser(it) }
    }

    private fun loadHorizontalGroup(student: Student, forceRefresh: Boolean) {
        flow {
            val selectedTiles = selectedDashboardTiles
            val flowSuccess = flowOf(Resource.Success(null))

            val luckyNumberFlow = luckyNumberRepository.getLuckyNumber(student, forceRefresh)
                .mapResourceData {
                    it ?: LuckyNumber(0, LocalDate.now(), 0)
                }
                .onResourceError { errorHandler.dispatch(it) }
                .takeIf { DashboardItem.Tile.LUCKY_NUMBER in selectedTiles } ?: flowSuccess

            val messageFlow = flatResourceFlow {
                val mailbox = messageRepository.getMailboxByStudent(student)

                messageRepository.getMessages(
                    student = student,
                    mailbox = mailbox,
                    folder = MessageFolder.RECEIVED,
                    forceRefresh = forceRefresh
                )
            }
                .mapResourceData { it.map { messageWithAuthor -> messageWithAuthor.message } }
                .onResourceError { errorHandler.dispatch(it) }
                .takeIf { DashboardItem.Tile.MESSAGES in selectedTiles } ?: flowSuccess

            val attendanceFlow = flatResourceFlow {
                val semester = semesterRepository.getCurrentSemester(student)
                attendanceSummaryRepository.getAttendanceSummary(
                    student = student,
                    semester = semester,
                    subjectId = -1,
                    forceRefresh = forceRefresh
                )
            }
                .onResourceError { errorHandler.dispatch(it) }
                .takeIf { DashboardItem.Tile.ATTENDANCE in selectedTiles } ?: flowSuccess

            emitAll(
                combine(
                    flow = luckyNumberFlow,
                    flow2 = messageFlow,
                    flow3 = attendanceFlow,
                ) { luckyNumberResource, messageResource, attendanceResource ->
                    val resList = listOf(luckyNumberResource, messageResource, attendanceResource)

                    resList to DashboardItem.HorizontalGroup(
                        attendancePercentage = DashboardItem.HorizontalGroup.Cell(
                            data = attendanceResource.dataOrNull?.calculatePercentage(),
                            error = attendanceResource.errorOrNull,
                            isLoading = attendanceResource is Resource.Loading,
                        ),
                        unreadMessagesCount = DashboardItem.HorizontalGroup.Cell(
                            data = messageResource.dataOrNull?.count { it.unread },
                            error = messageResource.errorOrNull,
                            isLoading = messageResource is Resource.Loading,
                        ),
                        luckyNumber = DashboardItem.HorizontalGroup.Cell(
                            data = luckyNumberResource.dataOrNull?.luckyNumber,
                            error = luckyNumberResource.errorOrNull,
                            isLoading = luckyNumberResource is Resource.Loading,
                        )
                    )
                })
        }
            .filterNot { (_, it) -> it.isLoading && forceRefresh }
            .distinctUntilChanged()
            .onEach { (_, it) ->
                updateData(it, forceRefresh)

                if (it.isLoading) {
                    Timber.i("Loading horizontal group data started")
                } else {
                    Timber.i("Loading horizontal group result: Success")
                }
            }
            .catch {
                Timber.i("Loading horizontal group result: An exception occurred")
                updateData(
                    DashboardItem.HorizontalGroup(selfError = it),
                    forceRefresh,
                )
                errorHandler.dispatch(it)
            }
            .launchWithUniqueRefreshJob("horizontal_group", forceRefresh)
    }

    private fun loadGrades(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)

            gradeRepository.getGrades(student, semester, forceRefresh)
        }
            .mapResourceData { (details, _) ->
                val filteredSubjectWithGrades = details
                    .filter { it.date >= LocalDate.now().minusDays(7) }
                    .groupBy { it.subject }
                    .mapValues { entry ->
                        entry.value
                            .take(5)
                            .sortedByDescending { it.date }
                    }
                    .toList()
                    .sortedByDescending { (_, grades) -> grades[0].date }
                    .toMap()

                filteredSubjectWithGrades
            }
            .logResourceStatus("Loading dashboard grades")
            .combine(preferencesRepository.gradeColorThemeFlow) { it, gradeColorTheme ->
                updateData(
                    DashboardItem.Grades(
                        subjectWithGrades = it.dataOrNull,
                        gradeTheme = gradeColorTheme,
                        isLoading = it is Resource.Loading,
                        error = it.errorOrNull
                    ), forceRefresh
                )
                it
            }
            .onResourceError { errorHandler.dispatch(it) }
            .launchWithUniqueRefreshJob("dashboard_grades", forceRefresh)
    }

    private fun loadLessons(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)
            val date = when (isStudentHasLessonsOnWeekendUseCase(semester)) {
                true -> LocalDate.now()
                else -> LocalDate.now().nextOrSameSchoolDay
            }

            timetableRepository.getTimetable(
                student = student,
                semester = semester,
                start = date,
                end = date.sunday,
                forceRefresh = forceRefresh,
            )
        }
            .logResourceStatus("Loading dashboard lessons")
            .onEach {
                updateData(
                    DashboardItem.Lessons(
                        lessons = it.dataOrNull,
                        isLoading = it is Resource.Loading,
                        error = it.errorOrNull
                    ), forceRefresh
                )
            }
            .onResourceError { errorHandler.dispatch(it) }
            .launchWithUniqueRefreshJob("dashboard_lessons", forceRefresh)
    }

    private fun loadHomework(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)
            val date = LocalDate.now().nextOrSameSchoolDay

            homeworkRepository.getHomework(
                student = student,
                semester = semester,
                start = date,
                end = date,
                forceRefresh = forceRefresh
            )
        }
            .mapResourceData { homework ->
                val currentDate = LocalDate.now()

                val filteredHomework = homework.filter {
                    it.date >= currentDate && !it.isDone
                }.sortedBy { it.date }

                filteredHomework
            }
            .logResourceStatus("Loading dashboard homework")
            .onEach {
                updateData(
                    DashboardItem.Homework(
                        homework = it.dataOrNull.orEmpty(),
                        isLoading = it is Resource.Loading,
                        error = it.errorOrNull
                    ), forceRefresh
                )
            }
            .onResourceError { errorHandler.dispatch(it) }
            .launchWithUniqueRefreshJob("dashboard_homework", forceRefresh)
    }

    private fun loadSchoolAnnouncements(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            schoolAnnouncementRepository.getSchoolAnnouncements(student, forceRefresh)
        }
            .logResourceStatus("Loading dashboard announcements")
            .onEach {
                updateData(
                    DashboardItem.Announcements(
                        announcement = it.dataOrNull.orEmpty(),
                        isLoading = it is Resource.Loading,
                        error = it.errorOrNull
                    ), forceRefresh
                )
            }
            .onResourceError { errorHandler.dispatch(it) }
            .launchWithUniqueRefreshJob("dashboard_announcements", forceRefresh)
    }

    private fun loadExams(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)

            examRepository.getExams(
                student = student,
                semester = semester,
                start = LocalDate.now(),
                end = LocalDate.now().plusDays(7),
                forceRefresh = forceRefresh
            )
        }
            .mapResourceData { exams -> exams.sortedBy { exam -> exam.date } }
            .logResourceStatus("Loading dashboard exams")
            .onEach {
                updateData(
                    DashboardItem.Exams(
                        exams = it.dataOrNull.orEmpty(),
                        isLoading = it is Resource.Loading,
                        error = it.errorOrNull
                    ), forceRefresh
                )
            }
            .onResourceError { errorHandler.dispatch(it) }
            .launchWithUniqueRefreshJob("dashboard_exams", forceRefresh)
    }

    private fun loadConferences(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)

            conferenceRepository.getConferences(
                student = student,
                semester = semester,
                forceRefresh = forceRefresh,
                startDate = Instant.now(),
            )
        }
            .logResourceStatus("Loading dashboard conferences")
            .onEach {
                updateData(
                    DashboardItem.Conferences(
                        conferences = it.dataOrNull.orEmpty(),
                        isLoading = it is Resource.Loading,
                        error = it.errorOrNull
                    ), forceRefresh
                )
            }
            .onResourceError { errorHandler.dispatch(it) }
            .launchWithUniqueRefreshJob("dashboard_conferences", forceRefresh)
    }

    private fun loadAdminMessage(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            getAppropriateAdminMessageUseCase(
                student = student,
                type = MessageType.DASHBOARD_MESSAGE,
            )
        }
            .logResourceStatus("Loading dashboard admin message")
            .onEach {
                updateData(
                    DashboardItem.AdminMessages(
                        adminMessage = it.dataOrNull,
                        isLoading = it is Resource.Loading,
                        error = it.errorOrNull
                    ), forceRefresh
                )
            }
            .onResourceError { Timber.e(it) }
            .launchWithUniqueRefreshJob("dashboard_admin_messages", forceRefresh)
    }

    private fun loadAds(forceRefresh: Boolean) {
        combine(
            adsHelper.isMobileAdsSdkInitialized.filter { it },
            preferencesRepository.isAdsEnabledFlow
        ) { _, adsEnabled ->
            if (!adsEnabled || !adsHelper.canShowAd) {
                // Can't just return - we need to make sure that if the user disabled ads they
                // are actually removed.
                updateData(DashboardItem.Ads(adBanner = null), forceRefresh)
                return@combine
            }

            updateData(DashboardItem.Ads(isLoading = true), false)
            val dashboardAdItem = runCatching {
                DashboardItem.Ads(adsHelper.getDashboardTileAdBanner(view!!.tileWidth))
            }.onFailure { Timber.e(it) }.getOrElse { DashboardItem.Ads(error = it) }

            updateData(dashboardAdItem, forceRefresh)
        }.launchIn(presenterScope)
    }

    private fun updateData(dashboardItem: DashboardItem, forceRefresh: Boolean) {
        if (forceRefresh && dashboardItem.isLoading) return
        if (!forceRefresh && dashboardItem.isDataLoaded && dashboardItem.isLoading) {
            firstLoadedItemList += dashboardItem.type
        }

        // Replace the existing item only if the new item isn't an error (because a snack bar will
        // be shown instead), or if this item doesn't yet exist on the dashboard yet.
        // It's better to show an error tile than nothing, and it's better to keep showing a tile
        // with old data than replace it with an error tile.
        if (dashboardItem.error == null || (!forceRefresh && dashboardItem.type !in firstLoadedItemList)) {
            dashboardItemLoadedList[dashboardItem.type] = dashboardItem
        }

        if (forceRefresh) {
            updateForceRefreshData(dashboardItem)
        } else {
            updateNormalData()
        }
    }

    private fun updateNormalData() {
        // Loading as in at least started to load
        val isLoading = dashboardItemsToLoad.all { type ->
            type.importance == NonBlocking || dashboardItemLoadedList.containsKey(type)
        }
        // Finished loading or at least has some meaningful intermediate data
        val isLoaded =
            isLoading && dashboardItemLoadedList.values.all(DashboardItem::isConsideredLoaded)

        if (isLoaded) {
            view?.run {
                showProgress(false)
                showErrorView(false)
                showContent(true)
                updateData()
            }
        }

        showErrorIfExists(
            isItemsLoaded = isLoading,
            itemsLoadedList = dashboardItemLoadedList,
            forceRefresh = false
        )
    }

    private fun updateForceRefreshData(dashboardItem: DashboardItem) {
        dashboardItemRefreshLoadedList[dashboardItem.type] = dashboardItem

        // Loading as in at least started to load
        val isLoading = dashboardItemsToLoad.all { type ->
            type.importance == NonBlocking || dashboardItemRefreshLoadedList.containsKey(type)
        }
        // Finished loading or at least has some meaningful intermediate data
        val isLoaded =
            isLoading && dashboardItemRefreshLoadedList.values.all(DashboardItem::isConsideredLoaded)

        if (isLoaded) {
            view?.run {
                showRefresh(false)
                showErrorView(false)
                showContent(true)
                updateData()
            }
        }

        showErrorIfExists(
            isItemsLoaded = isLoading,
            itemsLoadedList = dashboardItemRefreshLoadedList,
            forceRefresh = true
        )

        if (isLoaded) dashboardItemRefreshLoadedList.clear()
    }

    private fun showErrorIfExists(
        isItemsLoaded: Boolean,
        itemsLoadedList: Map<DashboardItem.Type, DashboardItem>,
        forceRefresh: Boolean
    ) {
        // Admin message probably indicates a problem with our own messages service, and account is
        // considered important enough to be handled specially - if it returns an error, all data
        // is presumed to be invalid too.
        val skippedItems = listOf(DashboardItem.Type.ADMIN_MESSAGE, DashboardItem.Tile.ACCOUNT)
        val checkIsGeneralError = { items: Map<DashboardItem.Type, DashboardItem> ->
            val filteredItems = items.filter { (type) -> type !in skippedItems }
            val isAccountItemError = items[DashboardItem.Type.ACCOUNT]?.error != null
            !filteredItems.values.any { it.error == null } || isAccountItemError
        }

        val isGeneralError = checkIsGeneralError(itemsLoadedList)
        if (isGeneralError && isItemsLoaded) {
            lastError = itemsLoadedList.values.firstNotNullOf { it.error }

            val wasGeneralError = checkIsGeneralError(dashboardItemLoadedList)
            val adminMessageItem =
                (itemsLoadedList[DashboardItem.Type.ADMIN_MESSAGE] as DashboardItem.AdminMessages?)?.takeIf { it.isDataLoaded }

            view?.run {
                showProgress(false)
                showRefresh(false)
                if ((forceRefresh && wasGeneralError) || !forceRefresh) {
                    showContent(false)
                    showErrorView(true, adminMessageItem)
                    setErrorDetails(lastError)
                }
            }
        }
    }

    private fun DashboardView.updateData() {
        val dashboardItemsPosition = preferencesRepository.dashboardItemsPosition ?: emptyMap()
        val items = dashboardItemLoadedList.values.toMutableList().also {
            it.retainAll(DashboardItem::canBeDisplayed)
            it.sortBy { item -> dashboardItemsPosition[item.type] ?: item.order }
        }
        updateData(items)
    }

    private fun Flow<Resource<*>>.launchWithUniqueRefreshJob(name: String, forceRefresh: Boolean) {
        val jobName = if (forceRefresh) "$name-forceRefresh" else name

        if (forceRefresh) {
            onEach {
                if (it is Resource.Success || it is Resource.Error) {
                    cancelJobs(jobName)
                }
            }.launch(jobName)
        } else {
            launch(jobName)
        }
    }

    @JvmName("launchWithUniqueRefreshJobHorizontalGroup")
    private fun Flow<Pair<List<Resource<*>>, *>>.launchWithUniqueRefreshJob(
        name: String,
        forceRefresh: Boolean
    ) {
        val jobName = if (forceRefresh) "$name-forceRefresh" else name

        if (forceRefresh) {
            onEach { (resources, _) ->
                if (resources.all { it is Resource.Success<*> }) {
                    cancelJobs(jobName)
                } else if (resources.any { it is Resource.Error<*> }) {
                    cancelJobs(jobName)
                }
            }.launch(jobName)
        } else {
            launch(jobName)
        }
    }
}
