package io.github.wulkanowy.data.repositories.local

import android.content.Context
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.security.Scrambler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentLocal @Inject constructor(private val studentDb: StudentDao,
                                       private val sharedPref: SharedPrefHelper,
                                       private val context: Context) {

    companion object {
        const val CURRENT_USER_KEY: String = "current_user_id"
    }

    val isStudentLoggedIn: Boolean
        get() = sharedPref.getLong(CURRENT_USER_KEY, 0) != 0L

    fun save(student: Student) {
        sharedPref.putLong(CURRENT_USER_KEY, studentDb.insert(student.apply {
            password = Scrambler.encrypt(password, context)
        }))
    }

    fun getCurrentStudent(): Single<Student> {
        return studentDb.load(sharedPref.getLong(CURRENT_USER_KEY, defaultValue = 0))
                .map { it.apply { password = Scrambler.decrypt(password) } }
    }
}