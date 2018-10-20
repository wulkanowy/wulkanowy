package io.github.wulkanowy.services

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ServiceManager {

    fun start(interval: Int, useOnlyWifi: Boolean) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(if (useOnlyWifi) NetworkType.NOT_ROAMING else NetworkType.CONNECTED)
            .build()

        val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(interval.toLong(), TimeUnit.MINUTES)
            .setConstraints(constraints).build()

        WorkManager.getInstance()
            .enqueueUniquePeriodicWork("SyncJob", ExistingPeriodicWorkPolicy.KEEP, syncWork)
    }
}
