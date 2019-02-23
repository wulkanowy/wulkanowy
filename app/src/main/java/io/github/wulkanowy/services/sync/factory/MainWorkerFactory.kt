package io.github.wulkanowy.services.sync.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class MainWorkerFactory @Inject constructor(
    private val factories: Map<Class<*>, @JvmSuppressWildcards Provider<InjectableWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        return try {
            factories.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }
                .let {
                    checkNotNull(it) { "Unknown worker class name: $workerClassName" }
                    it.value.get().create(appContext, workerParameters)
                }
        } catch (e: Exception) {
            Timber.e(e, "There was an error creating the worker")
            null
        }
    }
}
