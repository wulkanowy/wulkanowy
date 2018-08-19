package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Vulcan
import io.github.wulkanowy.api.login.AccountPermissionException
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import org.apache.commons.lang3.StringUtils.stripAccents
import javax.inject.Inject

class StudentRemote @Inject constructor(private val api: Vulcan) {

    fun getConnectedStudents(email: String, password: String, symbol: String): Single<List<Student>> {
        return Single.fromCallable {
            initApi(email, password, symbol)
            getSymbols().mapNotNull { symbol ->
                try {
                    initApi(email, password, symbol)
                    api.schools.flatMap { school ->
                        initApi(email, password, symbol, school.id)
                        api.studentAndParent.students.map { student ->
                            Student(
                                    email = email,
                                    password = password,
                                    symbol = symbol,
                                    studentId = student.id.toLong(),
                                    studentName = student.name,
                                    schoolId = school.id.toLong(),
                                    schoolName = school.name
                            )
                        }
                    }
                } catch (e: AccountPermissionException) {
                    null
                }
            }.flatten()
        }
    }

    private fun initApi(email: String, password: String, symbol: String, schoolId: String? = null) {
        api.apply {
            logout()
            setCredentials(email, password, symbol, schoolId, null, null)
        }
    }

    private fun getSymbols(): List<String> {
        return api.symbols.map {
            stripAccents(it.replace("[\\s \\W]".toRegex(), ""))
        }
    }
}