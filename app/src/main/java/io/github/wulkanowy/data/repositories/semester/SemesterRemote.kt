package io.github.wulkanowy.data.repositories.semester

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterRemote @Inject constructor(private val sdk: Sdk) {

    fun getSemesters(student: Student): Single<List<Semester>> {
        return sdk.getSemesters().map { semesters ->
            semesters.map { semester ->
                Semester(
                    studentId = student.studentId,
                    diaryId = semester.diaryId,
                    diaryName = semester.diaryName,
                    schoolYear = semester.schoolYear,
                    semesterId = semester.semesterId,
                    semesterName = semester.semesterNumber,
                    isCurrent = semester.current,
                    start = semester.start,
                    end = semester.end,
                    classId = semester.classId,
                    unitId = semester.unitId
                )
            }
        }
    }
}
