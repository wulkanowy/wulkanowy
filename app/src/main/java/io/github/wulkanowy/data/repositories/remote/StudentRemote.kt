package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRemote @Inject constructor(private val api: Api) {

    fun getConnectedStudents(email: String, password: String, symbol: String): Single<List<Student>> {
        api.let {
            it.email = email
            it.password = password
            it.symbol = symbol
        }
        return Single.just(emptyList())
    }
}
