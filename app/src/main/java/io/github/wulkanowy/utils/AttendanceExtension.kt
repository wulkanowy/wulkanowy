package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.AttendanceSummary

/**
 * [UONET+ - Zasady tworzenia podsumowań liczb uczniów obecnych i nieobecnych w tabeli frekwencji](https://www.vulcan.edu.pl/vulcang_files/user/AABW/AABW-PDF/uonetplus/uonetplus_Frekwencja-liczby-obecnych-nieobecnych.pdf)
 */
fun AttendanceSummary.calculateAttendance(): Double {
    val presence = getPresence()
    val absence = getAbsence()

    return calculateAttendance(presence, absence)
}

fun List<AttendanceSummary>.calculateAttendance(): Double {
    val presence = sumByDouble { it.getPresence() }
    val absence = sumByDouble { it.getAbsence() }

    return calculateAttendance(presence, absence)
}

private fun AttendanceSummary.getPresence(): Double {
    return presence.toDouble() + absenceForSchoolReasons + lateness + latenessExcused
}

private fun AttendanceSummary.getAbsence(): Double {
    return absence.toDouble() + absenceExcused
}

private fun calculateAttendance(presence: Double, absence: Double): Double {
    if (0.0 == (presence + absence)) {
        return 0.0
    }

    return (presence / (presence + absence)) * 100
}
