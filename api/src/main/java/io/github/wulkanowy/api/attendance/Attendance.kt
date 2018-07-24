package io.github.wulkanowy.api.attendance

import io.github.wulkanowy.api.ATTENDANCE_PAGE_URL
import io.github.wulkanowy.api.SnP
import io.github.wulkanowy.api.getDateAsTick
import io.github.wulkanowy.api.getFormattedDate
import org.jsoup.nodes.Element

class Attendance(private val snp: SnP) {

    fun getAttendance() = getAttendance("")

    fun getAttendance(start: String): List<AttendanceLesson> {
        val table = snp.getSnPPageDocument(ATTENDANCE_PAGE_URL + getDateAsTick(start))
                .selectFirst(".mainContainer .presentData")

        val days = getDays(table)

        val lessons = mutableListOf<AttendanceLesson>()
        table.select("tbody tr").map {
            val hours = it.select("td")
            hours.drop(1).mapIndexed { i, item ->
                lessons.add(addLessonDetails(AttendanceLesson(
                        date = days[i],
                        number = hours[0].text().toInt()
                ), item))
            }
        }

        return lessons.sortedBy {
            it.date
        }.filter {
            it.subject.isNotBlank()
        }
    }

    private fun getDays(table: Element): List<String> {
        return table.select("thead th").drop(1).map {
            getFormattedDate(it.html().split("<br>")[1])
        }
    }

    private fun addLessonDetails(lesson: AttendanceLesson, cell: Element): AttendanceLesson {
        lesson.subject = cell.select("span").text()

        if (LessonTypes.CLASS_NOT_EXIST == cell.attr("class")) {
            lesson.notExist = true
            lesson.empty = true

            return lesson
        }

        when (cell.select("div").attr("class")) {
            LessonTypes.CLASS_PRESENCE -> lesson.presence = true
            LessonTypes.CLASS_ABSENCE_UNEXCUSED -> lesson.absenceUnexcused = true
            LessonTypes.CLASS_ABSENCE_EXCUSED -> lesson.absenceExcused = true
            LessonTypes.CLASS_ABSENCE_FOR_SCHOOL_REASONS -> lesson.absenceForSchoolReasons = true
            LessonTypes.CLASS_UNEXCUSED_LATENESS -> lesson.unexcusedLateness = true
            LessonTypes.CLASS_EXCUSED_LATENESS -> lesson.excusedLateness = true
            LessonTypes.CLASS_EXEMPTION -> lesson.exemption = true

            else -> lesson.empty = true
        }

        return lesson
    }
}
