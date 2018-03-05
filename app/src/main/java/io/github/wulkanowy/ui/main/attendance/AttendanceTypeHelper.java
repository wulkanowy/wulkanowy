package io.github.wulkanowy.ui.main.attendance;

import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;

class AttendanceTypeHelper {

    static int getLessonDescription(AttendanceLesson lesson) {
        if (lesson.getIsAbsenceForSchoolReasons()) {
            return R.string.attendance_absence_for_school_reasons;
        }

        if (lesson.getIsAbsenceExcused()) {
            return R.string.attendance_absence_excused;
        }

        if (lesson.getIsAbsenceUnexcused()) {
            return R.string.attendance_absence_unexcused;
        }

        if (lesson.getIsExemption()) {
            return R.string.attendance_exemption;
        }

        if (lesson.getIsExcusedLateness()) {
            return R.string.attendance_excused_lateness;
        }

        if (lesson.getIsUnexcusedLateness()) {
            return R.string.attendance_unexcused_lateness;
        }

        return R.string.attendance_present;
    }
}
