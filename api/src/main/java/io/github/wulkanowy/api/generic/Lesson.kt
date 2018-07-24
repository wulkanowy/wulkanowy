package io.github.wulkanowy.api.generic

data class Lesson(

        var number: Int = 0,

        var subject: String = "",

        var teacher: String = "",

        var room: String = "",

        var description: String = "",

        var groupName: String = "",

        var startTime: String = "",

        var endTime: String = "",

        var date: String = "",

        var isEmpty: Boolean = false,

        var isDivisionIntoGroups: Boolean = false,

        var isPlanning: Boolean = false,

        var isRealized: Boolean = false,

        var isMovedOrCanceled: Boolean = false,

        var isNewMovedInOrChanged: Boolean = false
)
