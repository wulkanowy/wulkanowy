package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRemote @Inject constructor(private val api: Api) {

    fun getStudents(email: String, password: String, symbol: String, endpoint: String): Single<List<Student>> {
        return Single.just(
            api.apply {
                this.email = email
                this.password = password
                this.symbol = symbol
                this.host = URL(endpoint).run { host + ":$port".removeSuffix(":-1") }
                ssl = endpoint.startsWith("https")
                loginType = Api.LoginType.valueOf("AUTO")
            }
        ).flatMap {
            api.getPupils().map { students ->
                students.map { pupil ->
                    Student(
                        email = email,
                        password = password,
                        symbol = pupil.symbol,
                        studentId = pupil.studentId,
                        studentName = pupil.studentName,
                        schoolSymbol = pupil.schoolSymbol,
                        schoolName = pupil.schoolName,
                        endpoint = endpoint,
                        loginType = pupil.loginType.name
                    )
                }
            }
        }
    }
}

