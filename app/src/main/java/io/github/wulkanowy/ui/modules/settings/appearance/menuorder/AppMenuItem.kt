package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.Destination
import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
sealed class AppMenuItem {

    companion object {
        val defaultAppMenuItemList = setOf(
            DashboardAppMenuItem(),
            GradeAppMenuItem(),
            TimetableAppMenuItem(),
            AttendanceAppMenuItem(),
            ExamsAppMenuItem(),
            HomeworkAppMenuItem(),
            NoteAppMenuItem(),
            LuckyNumberAppMenuItem(),
            SchoolAnnouncementsAppMenuItem(),
            SchoolAndTeachersAppMenuItem(),
            MobileDevicesAppMenuItem(),
            ConferenceAppMenuItem(),
            MessageAppMenuItem()
        ).sortedBy { it.order }
    }

    // https://youtrack.jetbrains.com/issue/KT-38958
    abstract var order: Int

    abstract val icon: Int

    abstract val title: Int

    abstract val destinationType: Destination.Type

    @kotlinx.serialization.Serializable
    data class DashboardAppMenuItem(override var order: Int = 0) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_main_dashboard

        @Transient
        override val title = R.string.dashboard_title

        @Transient
        override val destinationType = Destination.Type.DASHBOARD
    }

    @kotlinx.serialization.Serializable
    data class GradeAppMenuItem(override var order: Int = 1) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_main_grade

        @Transient
        override val title = R.string.grade_title

        @Transient
        override val destinationType = Destination.Type.GRADE
    }

    @kotlinx.serialization.Serializable
    data class AttendanceAppMenuItem(override var order: Int = 2) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_main_attendance

        @Transient
        override val title = R.string.attendance_title

        @Transient
        override val destinationType = Destination.Type.ATTENDANCE
    }

    @kotlinx.serialization.Serializable
    data class TimetableAppMenuItem(override var order: Int = 3) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_main_timetable

        @Transient
        override val title = R.string.timetable_title

        @Transient
        override val destinationType = Destination.Type.TIMETABLE
    }

    @kotlinx.serialization.Serializable
    data class MessageAppMenuItem(override var order: Int = 4) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_more_messages

        @Transient
        override val title = R.string.message_title

        @Transient
        override val destinationType = Destination.Type.MESSAGE
    }

    @kotlinx.serialization.Serializable
    data class ExamsAppMenuItem(override var order: Int = 5) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_main_exam

        @Transient
        override val title = R.string.exam_title

        @Transient
        override val destinationType = Destination.Type.EXAM
    }

    @kotlinx.serialization.Serializable
    data class HomeworkAppMenuItem(override var order: Int = 6) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_more_homework

        @Transient
        override val title = R.string.homework_title

        @Transient
        override val destinationType = Destination.Type.HOMEWORK
    }

    @kotlinx.serialization.Serializable
    data class NoteAppMenuItem(override var order: Int = 7) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_more_note

        @Transient
        override val title = R.string.note_title

        @Transient
        override val destinationType = Destination.Type.NOTE
    }

    @kotlinx.serialization.Serializable
    data class LuckyNumberAppMenuItem(override var order: Int = 8) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_more_lucky_number

        @Transient
        override val title = R.string.lucky_number_title

        @Transient
        override val destinationType = Destination.Type.LUCKY_NUMBER
    }

    @kotlinx.serialization.Serializable
    data class ConferenceAppMenuItem(override var order: Int = 9) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_more_conferences

        @Transient
        override val title = R.string.conferences_title

        @Transient
        override val destinationType = Destination.Type.CONFERENCE
    }

    @kotlinx.serialization.Serializable
    data class SchoolAnnouncementsAppMenuItem(override var order: Int = 10) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_all_about

        @Transient
        override val title = R.string.school_announcement_title

        @Transient
        override val destinationType = Destination.Type.SCHOOL_ANNOUNCEMENT
    }

    @kotlinx.serialization.Serializable
    data class SchoolAndTeachersAppMenuItem(override var order: Int = 11) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_more_schoolandteachers

        @Transient
        override val title = R.string.schoolandteachers_title

        @Transient
        override val destinationType = Destination.Type.SCHOOL
    }

    @kotlinx.serialization.Serializable
    data class MobileDevicesAppMenuItem(override var order: Int = 12) : AppMenuItem() {

        @Transient
        override val icon = R.drawable.ic_more_mobile_devices

        @Transient
        override val title = R.string.mobile_devices_title

        @Transient
        override val destinationType = Destination.Type.MOBILE_DEVICE
    }
}
