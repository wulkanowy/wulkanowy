package io.github.wulkanowy.services.job

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class JobManager {

    fun start(interval: Int, useOnlyWifi: Boolean) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(if (useOnlyWifi) NetworkType.UNMETERED else NetworkType.CONNECTED)
            .build()

        val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(interval.toLong(), TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance()
            .enqueueUniquePeriodicWork(SyncWorker.WORK_TAG, ExistingPeriodicWorkPolicy.REPLACE, syncWork)
    }
}
