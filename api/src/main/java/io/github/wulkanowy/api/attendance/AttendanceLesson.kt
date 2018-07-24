package io.github.wulkanowy.api.attendance

data class AttendanceLesson(

        var number: Int = 0,

        var subject: String = "",

        var date: String = "",

        var empty: Boolean = false,

        var notExist: Boolean = false,

        var presence: Boolean = false,

        var absenceUnexcused: Boolean = false,

        var absenceExcused: Boolean = false,

        var unexcusedLateness: Boolean = false,

        var absenceForSchoolReasons: Boolean = false,

        var excusedLateness: Boolean = false,

        var exemption: Boolean = false
)
