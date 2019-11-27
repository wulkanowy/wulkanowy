package io.github.wulkanowy.data.repositories.teacher

import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Teacher
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRemote @Inject constructor(private val sdk: SdkHelper) {

    fun getTeachers(semester: Semester): Single<List<Teacher>> {
        return sdk.changeSemester(semester).getTeachers(semester.semesterId)
            .map { teachers ->
                teachers.map {
                    Teacher(
                        studentId = semester.studentId,
                        name = it.name,
                        subject = it.subject,
                        shortName = it.short,
                        classId = semester.classId
                    )
                }
            }
    }
}
