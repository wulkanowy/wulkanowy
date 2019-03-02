package io.github.wulkanowy.services.sync

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.SyncChannel
import timber.log.Timber
import javax.inject.Inject

class SyncManager @Inject constructor(
    private val syncChannel: SyncChannel,
    private val preferencesRepository: PreferencesRepository
) {

    fun initialize() {
        if (SDK_INT >= O) syncChannel.create()
        Timber.i("SyncManager was initialized")
    }

    fun start() {
        WorkManager.getInstance()
            .enqueueUniqueWork(SyncWorker::class.java.simpleName, REPLACE, OneTimeWorkRequest.from(SyncWorker::class.java))
    }
}
