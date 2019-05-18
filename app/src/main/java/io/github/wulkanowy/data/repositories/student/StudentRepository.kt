package io.github.wulkanowy.data.repositories.student

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.ApiHelper
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val local: StudentLocal,
    private val remote: StudentRemote,
    private val settings: InternetObservingSettings,
    private val apiHelper: ApiHelper
) {

    fun isStudentSaved(): Single<Boolean> = local.getStudents(false).isEmpty.map { !it }

    fun isCurrentStudentSet(): Single<Boolean> = local.getCurrentStudent(false).isEmpty.map { !it }

    fun getStudents(email: String, password: String, endpoint: String, symbol: String = ""): Single<List<Student>> {
        return ReactiveNetwork.checkInternetConnectivity(settings)
            .flatMap {
                apiHelper.initApi(email, password, symbol, endpoint)
                if (it) remote.getStudents(email, password, endpoint)
                else Single.error(UnknownHostException("No internet connection"))
            }
    }

    fun getSavedStudents(decryptPass: Boolean = true): Single<List<Student>> {
        return local.getStudents(decryptPass).toSingle(emptyList())
    }

    fun getCurrentStudent(decryptPass: Boolean = true): Single<Student> {
        return local.getCurrentStudent(decryptPass)
            .switchIfEmpty(Maybe.error(NoCurrentStudentException()))
            .toSingle()
    }

    fun saveStudents(students: List<Student>): Single<List<Long>> {
        return local.saveStudents(students)
    }

    fun switchStudent(student: Student): Completable {
        return local.setCurrentStudent(student)
    }

    fun logoutStudent(student: Student): Completable {
        return local.logoutStudent(student)
    }
}
