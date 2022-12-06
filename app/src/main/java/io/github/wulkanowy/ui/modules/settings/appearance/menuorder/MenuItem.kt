package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import io.github.wulkanowy.R
import kotlinx.serialization.Serializable

@Serializable
sealed class MenuItem(
    open val icon: Int,
    open val title: Int
) {

    // https://youtrack.jetbrains.com/issue/KT-38958
    abstract var order: Int

    @Serializable
    data class StartMenuItem(override var order: Int = 0) :
        MenuItem(R.drawable.ic_main_dashboard, R.string.dashboard_title)

    @Serializable
    data class GradeMenuItem(override var order: Int = 1) :
        MenuItem(R.drawable.ic_main_grade, R.string.grade_title)

    @Serializable
    data class AttendanceMenuItem(override var order: Int = 2) :
        MenuItem(R.drawable.ic_main_attendance, R.string.attendance_title)

    @Serializable
    data class TimetableMenuItem(override var order: Int = 3) :
        MenuItem(R.drawable.ic_main_timetable, R.string.timetable_title)

    @Serializable
    data class MessageMenuItem(override var order: Int = 4) :
        MenuItem(R.drawable.ic_more_messages, R.string.message_title)

    @Serializable
    data class ExamsMenuItem(override var order: Int = 5) :
        MenuItem(R.drawable.ic_main_exam, R.string.exam_title)

    @Serializable
    data class HomeworkMenuItem(override var order: Int = 6) :
        MenuItem(R.drawable.ic_more_homework, R.string.homework_title)

    @Serializable
    data class NoteMenuItem(override var order: Int = 7) :
        MenuItem(R.drawable.ic_more_note, R.string.note_title)

    @Serializable
    data class LuckyNumberMenuItem(override var order: Int = 8) :
        MenuItem(R.drawable.ic_more_lucky_number, R.string.lucky_number_title)

    @Serializable
    data class ConferenceMenuItem(override var order: Int = 9) :
        MenuItem(R.drawable.ic_more_conferences, R.string.conferences_title)

    @Serializable
    data class SchoolAnnouncementsMenuItem(override var order: Int = 10) :
        MenuItem(R.drawable.ic_all_about, R.string.school_announcement_title)

    @Serializable
    data class SchoolAndTeachersMenuItem(override var order: Int = 11) :
        MenuItem(R.drawable.ic_more_schoolandteachers, R.string.teachers_title)

    @Serializable
    data class MobileDevicesMenuItem(override var order: Int = 12) :
        MenuItem(R.drawable.ic_more_mobile_devices, R.string.mobile_devices_title)
}
