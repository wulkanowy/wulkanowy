package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import org.threeten.bp.LocalDateTime.now
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.sdk.pojo.Student as SdkStudent

@Singleton
class StudentRemote @Inject constructor(private val sdk: Sdk) {

    fun getStudents(): Single<List<Student>> {
        return sdk.getStudents().map { mapStudents(it, "", "", "") }
    }

    fun getStudents(email: String, password: String, endpoint: String): Single<List<Student>> {
        return sdk.getStudents().map { mapStudents(it, email, password, endpoint) }
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
