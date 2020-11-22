package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AppInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRemote @Inject constructor(private val sdk: Sdk, private val appInfo: AppInfo) {

    suspend fun getStudentsMobileApi(
        token: String,
        pin: String,
        symbol: String
    ): List<StudentWithSemesters> {
        return sdk.getStudentsFromMobileApi(token, pin, symbol, "").mapToEntities(appInfo = appInfo)
    }

    suspend fun getStudentsScrapper(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        symbol: String
    ): List<StudentWithSemesters> {
        return sdk.getStudentsFromScrapper(email, password, scrapperBaseUrl, symbol)
            .mapToEntities(password, appInfo)
    }

    suspend fun getStudentsHybrid(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        symbol: String
    ): List<StudentWithSemesters> {
        return sdk.getStudentsHybrid(email, password, scrapperBaseUrl, "", symbol)
            .mapToEntities(password, appInfo)
    }
}
