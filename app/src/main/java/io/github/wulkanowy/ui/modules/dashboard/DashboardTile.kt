package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.TimetableFull
import io.github.wulkanowy.data.db.entities.Homework as EntitiesHomework

sealed class DashboardTile(val type: Type) {

    abstract val error: Throwable?

    abstract val isLoading: Boolean

    abstract val isDataLoaded: Boolean

    data class Account(
        val student: Student? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardTile(Type.ACCOUNT) {

        override val isDataLoaded get() = student != null
    }

    data class HorizontalGroup(
        val unreadMessagesCount: Int? = null,
        val attendancePercentage: Double? = null,
        val luckyNumber: Int? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardTile(Type.HORIZONTAL_GROUP) {

        override val isDataLoaded
            get() = unreadMessagesCount != null || attendancePercentage != null || luckyNumber != null
    }

    data class Grades(
        val subjectWithGrades: Map<String, List<Grade>>? = null,
        val gradeTheme: String? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardTile(Type.GRADES) {

        override val isDataLoaded get() = subjectWithGrades != null
    }

    data class Lessons(
        val lessons: TimetableFull? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardTile(Type.LESSONS) {

        override val isDataLoaded get() = lessons != null
    }

    data class Homework(
        val homework: List<EntitiesHomework>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardTile(Type.HOMEWORK) {

        override val isDataLoaded get() = homework != null
    }

    data class Announcements(
        val announcement: List<SchoolAnnouncement>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardTile(Type.ANNOUNCEMENTS) {

        override val isDataLoaded get() = announcement != null
    }

    data class Exams(
        val exams: List<Exam>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardTile(Type.EXAMS) {

        override val isDataLoaded get() = exams != null
    }

    data class Conferences(
        val conferences: List<Conference>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardTile(Type.CONFERENCES) {

        override val isDataLoaded get() = conferences != null
    }

    enum class Type(val id: Int) {
        ACCOUNT(1),
        HORIZONTAL_GROUP(2),
        LESSONS(3),
        GRADES(4),
        HOMEWORK(5),
        ANNOUNCEMENTS(6),
        EXAMS(7),
        CONFERENCES(8),
        ADS(9)
    }

    enum class DataType {
        ACCOUNT,
        LUCKY_NUMBER,
        MESSAGES,
        ATTENDANCE,
        LESSONS,
        GRADES,
        HOMEWORK,
        ANNOUNCEMENTS,
        EXAMS,
        CONFERENCES,
        ADS
    }
}

fun DashboardTile.DataType.toDashboardType() = when (this) {
    DashboardTile.DataType.ACCOUNT -> DashboardTile.Type.ACCOUNT
    DashboardTile.DataType.LUCKY_NUMBER -> DashboardTile.Type.HORIZONTAL_GROUP
    DashboardTile.DataType.MESSAGES -> DashboardTile.Type.HORIZONTAL_GROUP
    DashboardTile.DataType.ATTENDANCE -> DashboardTile.Type.HORIZONTAL_GROUP
    DashboardTile.DataType.LESSONS -> DashboardTile.Type.LESSONS
    DashboardTile.DataType.GRADES -> DashboardTile.Type.GRADES
    DashboardTile.DataType.HOMEWORK -> DashboardTile.Type.HOMEWORK
    DashboardTile.DataType.ANNOUNCEMENTS -> DashboardTile.Type.ANNOUNCEMENTS
    DashboardTile.DataType.EXAMS -> DashboardTile.Type.EXAMS
    DashboardTile.DataType.CONFERENCES -> DashboardTile.Type.CONFERENCES
    DashboardTile.DataType.ADS -> DashboardTile.Type.ADS
}