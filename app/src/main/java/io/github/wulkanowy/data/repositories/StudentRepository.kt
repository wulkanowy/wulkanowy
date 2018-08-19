package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.local.StudentLocal
import io.github.wulkanowy.data.repositories.remote.StudentRemote
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(private val local: StudentLocal,
                                            private val remote: StudentRemote,
                                            private val settings: InternetObservingSettings) {

    private var cachedStudents = listOf<Student>()

    val isStudentLoggedIn: Boolean
        get() = local.isStudentLoggedIn

    fun getConnectedStudents(email: String, password: String): Single<List<Student>> {
        return if (cachedStudents.isEmpty()) {
            ReactiveNetwork.checkInternetConnectivity(settings)
                    .flatMap { isConnected ->
                        if (isConnected) remote.getConnectedStudents(email, password)
                        else Single.error<List<Student>>(UnknownHostException("No internet connection"))
                    }.doAfterSuccess { cachedStudents = it }
        } else Single.just(cachedStudents)
    }

    fun save(student: Student) {
        local.save(student)
    }

    fun getCurrentStudent(): Single<Student> = local.getCurrentStudent()

    fun clearCache() {
        cachedStudents = listOf()
    }
}