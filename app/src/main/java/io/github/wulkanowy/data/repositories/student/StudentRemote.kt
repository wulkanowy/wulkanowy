package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import org.threeten.bp.LocalDateTime.now
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.sdk.pojo.Student as SdkStudent

@Singleton
class StudentRemote @Inject constructor(private val sdk: Sdk) {

    fun getStudentsMobileApi(token: String, pin: String, symbol: String, apiKey: String): Single<List<Student>> {
        return sdk.getStudentsFromMobileApi(token, pin, symbol, apiKey).map { mapStudents(it, "", "", "") }
    }

    fun getStudentsScrapper(email: String, password: String, endpoint: String, symbol: String): Single<List<Student>> {
        return sdk.getStudentsFromScrapper(email, password, endpoint.startsWith("https"), URL(endpoint)
            .run { host + ":$port".removeSuffix(":-1") }, symbol).map { mapStudents(it, email, password, endpoint) }
    }

    fun getStudentsHybrid(email: String, password: String, endpoint: String, symbol: String, apiKey: String): Single<List<Student>> {
        return sdk.getStudentsHybrid(email, password, apiKey, endpoint.startsWith("https"), URL(endpoint)
            .run { host + ":$port".removeSuffix(":-1") }, symbol).map { mapStudents(it, email, password, endpoint) }
    }

    private fun mapStudents(students: List<SdkStudent>, email: String, password: String, endpoint: String): List<Student> {
        return students.map { student ->
            Student(
                email = email,
                password = password,
                symbol = student.symbol,
                studentId = student.studentId,
                userLoginId = student.userLoginId,
                studentName = student.studentName,
                schoolSymbol = student.schoolSymbol,
                schoolName = student.schoolName,
                className = student.className,
                classId = student.classId,
                scrapperBaseUrl = endpoint,
                loginType = student.loginType.name,
                isCurrent = false,
                registrationDate = now(),
                apiBaseUrl = student.apiHost,
                privateKey = student.privateKey,
                certificateKey = student.certificateKey,
                loginMode = student.loginMode.name
            )
        }
    }
}
