package io.github.wulkanowy.services.sync

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.WorkManager
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.base.WorkerConfiguration
import io.github.wulkanowy.services.sync.channels.SyncChannel
import timber.log.Timber
import javax.inject.Inject

class SyncManager @Inject constructor(
    private val syncChannel: SyncChannel,
    private val workers: Map<Class<*>, @JvmSuppressWildcards WorkerConfiguration>,
    private val preferencesRepository: PreferencesRepository
) {

    fun initialize() {
        if (SDK_INT >= O) syncChannel.create()
        Timber.i("SyncManager was initialized")
    }

    fun start() {
        if (preferencesRepository.isServiceEnabled) {
            workers.forEach {
                WorkManager.getInstance()
                    .enqueueUniquePeriodicWork(it.key.simpleName, REPLACE, it.value.periodicRequest)
            }
        }
    }
}
