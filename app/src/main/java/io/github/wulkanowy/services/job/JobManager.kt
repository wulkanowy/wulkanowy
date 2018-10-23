package io.github.wulkanowy.services.job

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import timber.log.Timber
import java.util.concurrent.TimeUnit

class JobManager {

    fun start(interval: Long, useOnlyWifi: Boolean) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(if (useOnlyWifi) NetworkType.UNMETERED else NetworkType.CONNECTED)
            .build()

        val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(interval, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance()
            .enqueueUniquePeriodicWork(SyncWorker.WORK_TAG, ExistingPeriodicWorkPolicy.REPLACE, syncWork)

        Timber.d("Sync work scheduled")
    }
}
