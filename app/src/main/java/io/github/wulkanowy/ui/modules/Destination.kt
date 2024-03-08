package io.github.wulkanowy.ui.modules

import androidx.fragment.app.Fragment
import io.github.wulkanowy.data.serializers.LocalDateSerializer
import io.github.wulkanowy.ui.modules.attendance.AttendanceFragment
import io.github.wulkanowy.ui.modules.calculator.CalculatorFragment
import io.github.wulkanowy.ui.modules.conference.ConferenceFragment
import io.github.wulkanowy.ui.modules.dashboard.DashboardFragment
import io.github.wulkanowy.ui.modules.exam.ExamFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.ui.modules.luckynumber.LuckyNumberFragment
import io.github.wulkanowy.ui.modules.luckynumber.history.LuckyNumberHistoryFragment
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.mobiledevice.MobileDeviceFragment
import io.github.wulkanowy.ui.modules.more.MoreFragment
import io.github.wulkanowy.ui.modules.note.NoteFragment
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment
import io.github.wulkanowy.ui.modules.schoolannouncement.SchoolAnnouncementFragment
import io.github.wulkanowy.ui.modules.settings.SettingsFragment
import io.github.wulkanowy.ui.modules.timetable.TimetableFragment
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
sealed class Destination {

    /*
    Type in children classes have to be as getter to avoid null in enums
    https://stackoverflow.com/questions/68866453/kotlin-enum-val-is-returning-null-despite-being-set-at-compile-time
    */
    abstract val destinationType: Type

    abstract val destinationFragment: Fragment

    enum class Type(val defaultDestination: Destination) {
        DASHBOARD(Dashboard),
        GRADE(Grade),
        ATTENDANCE(Attendance),
        EXAM(Exam),
        TIMETABLE(Timetable()),
        HOMEWORK(Homework),
        NOTE(Note),
        CONFERENCE(Conference),
        SCHOOL_ANNOUNCEMENT(SchoolAnnouncement),
        SCHOOL_AND_TEACHERS(SchoolAndTeachers),
        LUCKY_NUMBER(LuckyNumber),
        LUCKY_NUMBER_HISTORY(LuckyNumberHistory),
        MORE(More),
        MESSAGE(Message),
        MOBILE_DEVICE(MobileDevice),
        AVERAGE_CALCULATOR(AverageCalculator),
        SETTINGS(Settings);
    }


    @Serializable
    data object Dashboard : Destination() {
        override val destinationType get() = Type.DASHBOARD
        override val destinationFragment get() = DashboardFragment.newInstance()
    }

    @Serializable
    data object Grade : Destination() {
        override val destinationType get() = Type.GRADE
        override val destinationFragment get() = GradeFragment.newInstance()
    }

    @Serializable
    data object Attendance : Destination() {
        override val destinationType get() = Type.ATTENDANCE
        override val destinationFragment get() = AttendanceFragment.newInstance()
    }

    @Serializable
    data object Exam : Destination() {
        override val destinationType get() = Type.EXAM
        override val destinationFragment get() = ExamFragment.newInstance()
    }

    @Serializable
    data class Timetable(
        @Serializable(with = LocalDateSerializer::class)
        private val date: LocalDate? = null
    ) : Destination() {
        override val destinationType get() = Type.TIMETABLE
        override val destinationFragment get() = TimetableFragment.newInstance(date)
    }

    @Serializable
    data object Homework : Destination() {
        override val destinationType get() = Type.HOMEWORK
        override val destinationFragment get() = HomeworkFragment.newInstance()
    }

    @Serializable
    data object Note : Destination() {
        override val destinationType get() = Type.NOTE
        override val destinationFragment get() = NoteFragment.newInstance()
    }

    @Serializable
    data object Conference : Destination() {
        override val destinationType get() = Type.CONFERENCE
        override val destinationFragment get() = ConferenceFragment.newInstance()
    }

    @Serializable
    data object SchoolAnnouncement : Destination() {
        override val destinationType get() = Type.SCHOOL_ANNOUNCEMENT
        override val destinationFragment get() = SchoolAnnouncementFragment.newInstance()
    }

    @Serializable
    data object SchoolAndTeachers : Destination() {
        override val destinationType get() = Type.SCHOOL_AND_TEACHERS
        override val destinationFragment get() = SchoolAndTeachersFragment.newInstance()
    }

    @Serializable
    data object LuckyNumber : Destination() {
        override val destinationType get() = Type.LUCKY_NUMBER
        override val destinationFragment get() = LuckyNumberFragment.newInstance()
    }

    @Serializable
    data object LuckyNumberHistory : Destination() {
        override val destinationType get() = Type.LUCKY_NUMBER_HISTORY
        override val destinationFragment get() = LuckyNumberHistoryFragment.newInstance()
    }

    @Serializable
    data object More : Destination() {
        override val destinationType get() = Type.MORE
        override val destinationFragment get() = MoreFragment.newInstance()
    }

    @Serializable
    data object Message : Destination() {
        override val destinationType get() = Type.MESSAGE
        override val destinationFragment get() = MessageFragment.newInstance()
    }

    @Serializable
    data object MobileDevice : Destination() {
        override val destinationType get() = Type.MOBILE_DEVICE
        override val destinationFragment get() = MobileDeviceFragment.newInstance()
    }

    @Serializable
    data object Settings : Destination() {
        override val destinationType get() = Type.SETTINGS
        override val destinationFragment get() = SettingsFragment.newInstance()
    }

    @Serializable
    data object AverageCalculator : Destination() {
        override val destinationType: Type get() = Type.AVERAGE_CALCULATOR
        override val destinationFragment: Fragment get() = CalculatorFragment.newInstance()
    }
}
