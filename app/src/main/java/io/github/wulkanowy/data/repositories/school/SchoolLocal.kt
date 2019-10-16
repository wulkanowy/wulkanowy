package io.github.wulkanowy.data.repositories.school

import io.github.wulkanowy.data.db.dao.SchoolInfoDao
import io.github.wulkanowy.data.db.entities.SchoolInfo
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Inject

class SchoolLocal @Inject constructor(private val schoolDb: SchoolInfoDao) {

    fun saveSchoolInfo(schoolInfo: SchoolInfo) {
        schoolDb.insert(schoolInfo)
    }

    fun deleteSchoolInfo(schoolInfo: SchoolInfo) {
        schoolDb.delete(schoolInfo)
    }

    fun getSchoolInfo(semester: Semester): Maybe<SchoolInfo> {
        return schoolDb.load(semester.studentId, semester.classId)
    }
}
