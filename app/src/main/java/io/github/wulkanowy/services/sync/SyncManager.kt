package io.github.wulkanowy.services.sync

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import io.github.wulkanowy.services.sync.channels.SyncChannel
import io.github.wulkanowy.services.sync.workers.grade.GradeWorker
import javax.inject.Inject

class SyncManager @Inject constructor(private val syncChannel: SyncChannel) {

    fun initialize() {
        if (SDK_INT >= O) syncChannel.create()
    }

    fun start() {
        WorkManager.getInstance()
            .enqueueUniqueWork("Grade", REPLACE, OneTimeWorkRequest.Builder(GradeWorker::class.java).build())
    }
}

