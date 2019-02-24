package io.github.wulkanowy.services.sync.workers.grade

import androidx.work.BackoffPolicy.EXPONENTIAL
import androidx.work.Constraints
import androidx.work.NetworkType.METERED
import androidx.work.NetworkType.UNMETERED
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.base.WorkerConfiguration
import java.util.concurrent.TimeUnit.MINUTES
import javax.inject.Inject

class GradeWorkerConfiguration @Inject constructor(private val preferencesRepository: PreferencesRepository) :
    WorkerConfiguration {

    override val periodicRequest: PeriodicWorkRequest
        get() {
            return PeriodicWorkRequest.Builder(GradeWorker::class.java, preferencesRepository.servicesInterval, MINUTES)
                .addTag(GradeWorker.WORKER_TAG)
                .setBackoffCriteria(EXPONENTIAL, 30, MINUTES)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(if (preferencesRepository.isServicesOnlyWifi) METERED else UNMETERED)
                    .build())
                .build()
        }

    override val oneTimeRequest: OneTimeWorkRequest
        get() = OneTimeWorkRequest.from(GradeWorker::class.java)
}
