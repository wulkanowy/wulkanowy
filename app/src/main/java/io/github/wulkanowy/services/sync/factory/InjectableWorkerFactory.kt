package io.github.wulkanowy.services.sync.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface InjectableWorkerFactory {

    fun create(appContext: Context, workerParameters: WorkerParameters): ListenableWorker
}
