package io.github.wulkanowy.services.sync.workers.grade

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
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
    private val gradeNotification: GradeNotification,
    private val preferencesRepository: PreferencesRepository
) : RxWorker(appContext, workerParameters) {

    companion object {
        const val WORKER_TAG = "GRADE_SYNC"
    }

    override fun createWork(): Single<Result> {
        return studentRepository.getCurrentStudent()
            .flatMap { student -> semesterRepository.getCurrentSemester(student).map { student to it } }
            .flatMap { data ->
                gradeRepository.getGrades(data.first, data.second, forceRefresh = true, notify = preferencesRepository.isNotificationsEnable)
                    .flatMap { gradeRepository.getNewGrades(data.second) }
            }
            .map { it.filter { grade -> !grade.isNotified } }
            .flatMapCompletable {
                if (it.isNotEmpty()) gradeNotification.notify(it)
                gradeRepository.updateGrades(it.onEach { grade -> grade.isNotified = true })
            }.toSingleDefault(Result.success())
            .onErrorReturn { Result.failure() }
    }

    @AssistedInject.Factory
    interface Factory : InjectableWorkerFactory
}
