package io.github.wulkanowy.services.sync.workers.attendance

import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.base.WorkerConfiguration
import java.util.concurrent.TimeUnit.MINUTES
import javax.inject.Inject

class AttendanceWorkerConfiguration @Inject constructor(private val preferencesRepository: PreferencesRepository) :
    WorkerConfiguration {

    override val periodicRequest: PeriodicWorkRequest
        get() {
            return PeriodicWorkRequest.Builder(AttendanceWorker::class.java, preferencesRepository.servicesInterval, MINUTES)
                .build()
        }

    override val oneTimeRequest: OneTimeWorkRequest
        get() = OneTimeWorkRequest.from(AttendanceWorker::class.java)
}

