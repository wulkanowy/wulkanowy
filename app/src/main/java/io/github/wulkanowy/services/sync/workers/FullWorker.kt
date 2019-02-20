package io.github.wulkanowy.services.sync.workers

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.factory.InjectableWorkerFactory
import io.reactivex.Single
import timber.log.Timber

class FullWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val studentRepository: StudentRepository
) : RxWorker(appContext, workerParameters) {

    override fun createWork(): Single<Result> {
        return studentRepository.isStudentSaved()
            .map { Result.success() }
            .onErrorReturn { Result.failure() }
            .doOnSuccess { Timber.i("SUCCESS") }
    }

    @AssistedInject.Factory
    interface Factory : InjectableWorkerFactory
}
