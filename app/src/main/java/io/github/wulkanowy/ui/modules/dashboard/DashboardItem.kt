package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.GradeColorTheme
import io.github.wulkanowy.data.pojos.TimetableFull
import io.github.wulkanowy.utils.AdBanner
import io.github.wulkanowy.data.db.entities.Homework as EntitiesHomework

sealed class DashboardItem(
    val type: Type,
    val order: Int = type.ordinal + 100,
) {

    abstract val error: Throwable?

    /**
     * If true, the item is currently being loaded.
     */
    abstract val isLoading: Boolean

    /**
     * If true, any data has been loaded
     */
    abstract val isDataLoaded: Boolean

    data class AdminMessages(
        val adminMessage: AdminMessage? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.ADMIN_MESSAGE, order = -1) {

        override val isDataLoaded get() = adminMessage != null
    }

    data class Account(
        val student: Student? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.ACCOUNT) {

        override val isDataLoaded get() = student != null
    }

    data class HorizontalGroup(
        val unreadMessagesCount: Cell<Int>? = null,
        val attendancePercentage: Cell<Double>? = null,
        val luckyNumber: Cell<Int>? = null,
        val selfError: Throwable? = null,
    ) : DashboardItem(Type.HORIZONTAL_GROUP) {

        data class Cell<T>(
            val data: T? = null,
            val error: Throwable? = null,
            val isLoading: Boolean = false,
        ) {
            val isHidden: Boolean
                get() = data == null && error == null && !isLoading
        }

        private val cells = listOfNotNull(unreadMessagesCount, attendancePercentage, luckyNumber)

        override val error: Throwable? = selfError ?: cells.map { it.error }.let { errors ->
            if (errors.all { it != null }) {
                errors.firstOrNull()
            } else null
        }

        override val isLoading = cells.any { it.isLoading }
        override val isDataLoaded = cells.any { !it.isLoading }
        val isFullDataLoaded = cells.all { !it.isLoading }
    }

    data class Grades(
        val subjectWithGrades: Map<String, List<Grade>>? = null,
        val gradeTheme: GradeColorTheme? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.GRADES) {

        override val isDataLoaded get() = !subjectWithGrades.isNullOrEmpty()
    }

    data class Lessons(
        val lessons: TimetableFull? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.LESSONS) {

        override val isDataLoaded get() = !lessons?.lessons.isNullOrEmpty()
    }

    data class Homework(
        val homework: List<EntitiesHomework>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.HOMEWORK) {

        override val isDataLoaded get() = !homework.isNullOrEmpty()
    }

    data class Announcements(
        val announcement: List<SchoolAnnouncement>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.ANNOUNCEMENTS) {

        override val isDataLoaded get() = !announcement.isNullOrEmpty()
    }

    data class Exams(
        val exams: List<Exam>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.EXAMS) {

        override val isDataLoaded get() = !exams.isNullOrEmpty()
    }

    data class Conferences(
        val conferences: List<Conference>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.CONFERENCES) {

        override val isDataLoaded get() = !conferences.isNullOrEmpty()
    }

    data class Ads(
        val adBanner: AdBanner? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.ADS) {

        override val isDataLoaded get() = adBanner != null
    }

    enum class Type(
        val refreshBehavior: RefreshBehavior = RefreshBehavior.OnScreen,
        val importance: Importance = Importance.Blocking,
        val loadingDisplay: LoadingDisplay = LoadingDisplay.Shown,
        val reorderable: Reorderable = Reorderable.Yes,
    ) {
        ADMIN_MESSAGE(
            refreshBehavior = RefreshBehavior.Always,
            importance = Importance.NonBlocking,
            loadingDisplay = LoadingDisplay.Hidden,
            reorderable = Reorderable.No,
        ),
        ACCOUNT(
            reorderable = Reorderable.No,
        ),
        HORIZONTAL_GROUP,
        LESSONS,
        ADS(
            importance = Importance.NonBlocking,
            loadingDisplay = LoadingDisplay.Hidden,
        ),
        GRADES,
        HOMEWORK,
        ANNOUNCEMENTS,
        EXAMS,
        CONFERENCES,
    }

    enum class Tile(
        val type: Type,
        /**
         * Always enabled tiles are not toggleable through the settings and are always processed
         * in the dashboard (but not necessarily actually displayed).
         */
        val alwaysEnabled: Boolean = false
    ) {
        ADMIN_MESSAGE(Type.ADMIN_MESSAGE, alwaysEnabled = true),
        ACCOUNT(Type.ACCOUNT, alwaysEnabled = true),
        LUCKY_NUMBER(Type.HORIZONTAL_GROUP),
        MESSAGES(Type.HORIZONTAL_GROUP),
        ATTENDANCE(Type.HORIZONTAL_GROUP),
        LESSONS(Type.LESSONS),
        ADS(Type.ADS, alwaysEnabled = true),
        GRADES(Type.GRADES),
        HOMEWORK(Type.HOMEWORK),
        ANNOUNCEMENTS(Type.ANNOUNCEMENTS),
        EXAMS(Type.EXAMS),
        CONFERENCES(Type.CONFERENCES);

        companion object {
            fun allAlwaysEnabled() = entries.filter(Tile::alwaysEnabled)
        }
    }

    enum class RefreshBehavior {
        /**
         * Types with this refresh behavior should always be refreshed, no matter whether they are
         * selected (in the dashboard tile list menu) or not.
         */
        Always,

        /**
         * Types with this refresh behavior are only refreshed when they are actually selected (in
         * the dashboard tile list menu).
         */
        OnScreen,
    }

    enum class Importance {
        /**
         * Items that do not block the whole dashboard from transitioning from a full-screen spinner
         * to the actual view.
         */
        NonBlocking,

        /**
         * Upon first loading the dashboard will continue to show a full-screen spinner until all
         * blocking items are loaded
         */
        Blocking,
    }

    enum class LoadingDisplay {
        /**
         * Tile does not support displaying it until there is data present.
         */
        Hidden,

        /**
         * Tile supports displaying it even when there isn't any data present, i.e. the item has
         * it's own loading state that can be independently displayed.
         */
        Shown
    }

    enum class Reorderable {
        Yes, No
    }
}

val DashboardItem.canBeDisplayed: Boolean
    get() = when (type.loadingDisplay) {
        DashboardItem.LoadingDisplay.Hidden -> isDataLoaded || error != null
        // Items that are Blocking & Shown do block the UI until the first updateData with their
        // respective data is invoked.
        DashboardItem.LoadingDisplay.Shown -> true
    }

val DashboardItem.isConsideredLoaded: Boolean
    get() = type.importance == DashboardItem.Importance.NonBlocking || canBeDisplayed
