package io.github.wulkanowy.services.sync.works

import androidx.work.WorkerParameters
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student

interface Work {

    val name: String get() = this::class.java.simpleName

    suspend fun doWork(student: Student, semester: Semester) {}

    suspend fun doWork(student: Student, semester: Semester, params: WorkerParameters) {
        doWork(student, semester)
    }

    suspend fun onFailure(params: WorkerParameters, e: Throwable) {}
}
