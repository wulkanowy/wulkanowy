package io.github.wulkanowy.data.repositories.semester

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.isCurrent
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterRepository @Inject constructor(
    private val remote: SemesterRemote,
    private val local: SemesterLocal,
    private val settings: InternetObservingSettings
) {

    suspend fun getSemesters(student: Student, forceRefresh: Boolean = false, refreshOnNoCurrent: Boolean = false): List<Semester> {
        return local.getSemesters(student).filter { !forceRefresh }.takeIf { semesters ->
            when {
                Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.API -> semesters.firstOrNull { it.isCurrent }?.diaryId != 0
                refreshOnNoCurrent -> semesters.any { semester -> semester.isCurrent }
                else -> true
            }
        } ?: run {
            val new = remote.getSemesters(student)
            if (new.isEmpty()) throw IllegalArgumentException("Empty semester list!")

            val old = local.getSemesters(student)
            local.deleteSemesters(old.uniqueSubtract(new))
            local.saveSemesters(new.uniqueSubtract(old))

            return local.getSemesters(student)
        }
    }

    suspend fun getCurrentSemester(student: Student, forceRefresh: Boolean = false): Semester {
        return getSemesters(student, forceRefresh).single { it.isCurrent }
    }
}
