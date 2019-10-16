package io.github.wulkanowy.data.repositories.school

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.SchoolInfo
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import javax.inject.Inject

class SchoolRemote @Inject constructor(private val api: Api) {

    fun getSchoolInfo(semester: Semester): Single<SchoolInfo> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { it.getSchool() }
            .map {
                SchoolInfo(
                    studentId = semester.studentId,
                    classId = semester.classId,
                    name = it.name,
                    address = it.address,
                    contact = it.contact,
                    headmaster = it.headmaster,
                    pedagogue = it.pedagogue
                )
            }
    }
}
