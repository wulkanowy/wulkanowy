package io.github.wulkanowy.services.sync.workers.grade

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.factory.InjectableWorkerFactory
import io.reactivex.Single

class GradeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val gradeRepository: GradeRepository,
    private val gradeNotification: GradeNotification
) : RxWorker(appContext, workerParameters) {

    override fun createWork(): Single<Result> {
        return studentRepository.getCurrentStudent()
            .flatMap { student -> semesterRepository.getCurrentSemester(student).map { student to it } }
            .flatMap { gradeRepository.getGrades(it.first, it.second, true) }
            .doOnSuccess { gradeNotification.notify(listOf(it.first())) }
            .map { Result.success() }
            .onErrorReturn { Result.failure() }
    }

    @AssistedInject.Factory
    interface Factory : InjectableWorkerFactory
}
