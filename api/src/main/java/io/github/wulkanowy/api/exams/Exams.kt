package io.github.wulkanowy.api.exams

import io.github.wulkanowy.api.SnP
import io.github.wulkanowy.api.getDateAsTick
import io.github.wulkanowy.api.getFormattedDate

class Exams(private val snp: SnP) {

    fun getExams() = getExams("")

    fun getExams(start: String): List<ExamEntry> {
        return snp.getSnPPageDocument("Sprawdziany.mvc/Terminarz?rodzajWidoku=2&data=" + getDateAsTick(start))
                .select(".mainContainer > div:not(.navigation)").map {
            val date = getFormattedDate(it.selectFirst("h2")?.text()?.split(", ")?.last()?.trim())

            it.select("article").map {
                val subjectAndGroup = snp.getRowDataChildValue(it, 1)
                val groupAndClass = subjectAndGroup.split(" ").last()
                val group = if (groupAndClass.contains("|")) groupAndClass.split("|").last() else ""
                val teacherAndDate = snp.getRowDataChildValue(it, 4).split(", ")
                val teacherSymbol = teacherAndDate.first().split(" ").last().removeSurrounding("[", "]")
                val teacher = teacherAndDate.first().replace(" [$teacherSymbol]", "")

                ExamEntry(
                        date = date,
                        entryDate = getFormattedDate(teacherAndDate.last()),
                        subject = subjectAndGroup.replace(groupAndClass, "").trim(),
                        group = group,
                        type = snp.getRowDataChildValue(it, 2),
                        description = snp.getRowDataChildValue(it, 3),
                        teacher = teacher,
                        teacherSymbol = teacherSymbol
                )
            }
        }.flatten()
    }
}
