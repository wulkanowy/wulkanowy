package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import io.github.wulkanowy.R

@kotlinx.serialization.Serializable
sealed class MenuItem {

    // https://youtrack.jetbrains.com/issue/KT-38958
    abstract var order: Int

    abstract val icon: Int

    abstract val title: Int

    @kotlinx.serialization.Serializable
    data class StartMenuItem(override var order: Int = 0) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_main_dashboard

        @kotlinx.serialization.Transient
        override val title = R.string.dashboard_title
    }

    @kotlinx.serialization.Serializable
    data class GradeMenuItem(override var order: Int = 1) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_main_grade

        @kotlinx.serialization.Transient
        override val title = R.string.grade_title
    }

    @kotlinx.serialization.Serializable
    data class AttendanceMenuItem(override var order: Int = 2) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_main_attendance

        @kotlinx.serialization.Transient
        override val title = R.string.attendance_title
    }

    @kotlinx.serialization.Serializable
    data class TimetableMenuItem(override var order: Int = 3) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_main_timetable

        @kotlinx.serialization.Transient
        override val title = R.string.timetable_title
    }

    @kotlinx.serialization.Serializable
    data class MessageMenuItem(override var order: Int = 4) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_more_messages

        @kotlinx.serialization.Transient
        override val title = R.string.message_title
    }

    @kotlinx.serialization.Serializable
    data class ExamsMenuItem(override var order: Int = 5) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_main_exam

        @kotlinx.serialization.Transient
        override val title = R.string.exam_title
    }

    @kotlinx.serialization.Serializable
    data class HomeworkMenuItem(override var order: Int = 6) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_more_homework

        @kotlinx.serialization.Transient
        override val title = R.string.homework_title
    }

    @kotlinx.serialization.Serializable
    data class NoteMenuItem(override var order: Int = 7) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_more_note

        @kotlinx.serialization.Transient
        override val title = R.string.note_title
    }

    @kotlinx.serialization.Serializable
    data class LuckyNumberMenuItem(override var order: Int = 8) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_more_lucky_number

        @kotlinx.serialization.Transient
        override val title = R.string.lucky_number_title
    }

    @kotlinx.serialization.Serializable
    data class ConferenceMenuItem(override var order: Int = 9) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_more_conferences

        @kotlinx.serialization.Transient
        override val title = R.string.conferences_title
    }

    @kotlinx.serialization.Serializable
    data class SchoolAnnouncementsMenuItem(override var order: Int = 10) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_all_about

        @kotlinx.serialization.Transient
        override val title = R.string.school_announcement_title
    }

    @kotlinx.serialization.Serializable
    data class SchoolAndTeachersMenuItem(override var order: Int = 11) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_more_schoolandteachers

        @kotlinx.serialization.Transient
        override val title = R.string.schoolandteachers_title
    }

    @kotlinx.serialization.Serializable
    data class MobileDevicesMenuItem(override var order: Int = 12) : MenuItem() {

        @kotlinx.serialization.Transient
        override val icon = R.drawable.ic_more_mobile_devices

        @kotlinx.serialization.Transient
        override val title = R.string.mobile_devices_title
    }
}
