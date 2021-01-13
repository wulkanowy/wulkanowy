package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.StudentGuardianDao
import io.github.wulkanowy.data.db.dao.StudentInfoDao
import io.github.wulkanowy.sdk.Sdk
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentInfoRepository @Inject constructor(
    private val studentInfoDao: StudentInfoDao,
    private val studentGuardianDao: StudentGuardianDao,
    private val sdk: Sdk
) {

}