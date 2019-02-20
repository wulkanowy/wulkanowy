package io.github.wulkanowy.services.sync.grade

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.factory.InjectableWorkerFactory
import io.reactivex.Single

class GradeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val gradeRepository: GradeRepository
) : RxWorker(appContext, workerParameters) {

    companion object {
        const val WORKER_TAG = "GradeWorker"
    }

    override fun createWork(): Single<Result> {
        TODO()
    }

    @AssistedInject.Factory
    interface Factory : InjectableWorkerFactory
}
