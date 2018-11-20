package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterRemote @Inject constructor(private val api: Api) {

    fun getSemesters(student: Student): Single<List<Semester>> {
        return Single.just(
            api.apply {
                email = student.email
                password = student.password
                symbol = student.symbol
                schoolSymbol = student.schoolSymbol
                studentId = student.studentId
                host = URL(student.endpoint).run { host + ":$port".removeSuffix(":-1") }
                ssl = student.endpoint.startsWith("https")
                loginType = Api.LoginType.valueOf(student.loginType)
            }
        ).flatMap {
            api.getSemesters().map { semesters ->
                semesters.map { semester ->
                    Semester(
                        studentId = student.studentId,
                        diaryId = semester.diaryId,
                        diaryName = semester.diaryName,
                        semesterId = semester.semesterId,
                        semesterName = semester.semesterNumber,
                        isCurrent = semester.current
                    )
                }

            }
        }
    }
}

