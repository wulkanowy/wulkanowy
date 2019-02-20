package io.github.wulkanowy.services.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class MainWorkerFactory @Inject constructor(
    private val factories: Map<Class<*>, @JvmSuppressWildcards Provider<InjectableWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        return factories.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }
            .let {
                checkNotNull(it) { "Unknown worker class name: $workerClassName" }
                it.value.get().create(appContext, workerParameters)
            }
    }
}
