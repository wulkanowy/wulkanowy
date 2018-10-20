package io.github.wulkanowy.services

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ServiceManager {

    private val jobTag = "SyncJob"

    private val interval = 15L

    private val useOnlyWifi = true

    fun start() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(if (useOnlyWifi) NetworkType.NOT_ROAMING else NetworkType.CONNECTED)
            .build()

        val uploadWork = PeriodicWorkRequestBuilder<SyncWorker>(interval, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(jobTag)
            .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(jobTag, ExistingPeriodicWorkPolicy.KEEP, uploadWork)
    }
}
