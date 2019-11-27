package io.github.wulkanowy.data.repositories.subject

import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Subject
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRemote @Inject constructor(private val sdk: SdkHelper) {

    fun getSubjects(semester: Semester): Single<List<Subject>> {
        return sdk.changeSemester(semester).getSubjects()
            .map { subjects ->
                subjects.map {
                    Subject(
                        studentId = semester.studentId,
                        diaryId = semester.diaryId,
                        name = it.name,
                        realId = it.id
                    )
                }
            }
    }
}
