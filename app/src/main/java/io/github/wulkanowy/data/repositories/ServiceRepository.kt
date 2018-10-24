package io.github.wulkanowy.data.repositories

import com.firebase.jobdispatcher.Constraint
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.RetryStrategy
import com.firebase.jobdispatcher.Trigger
import io.github.wulkanowy.services.job.SyncWorker
import io.github.wulkanowy.utils.isHolidays
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepository @Inject constructor(
    private val prefRepository: PreferencesRepository,
    private val dispatcher: FirebaseJobDispatcher
) {

    fun reloadFullSyncService() {
        stopFullSyncService()

        if (!LocalDate.now().isHolidays && prefRepository.serviceEnables) {
            startFullSyncService()
        }
    }

    fun startFullSyncService() {
        dispatcher.mustSchedule(
            dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(SyncWorker::class.java)
                .setTag(SyncWorker.WORK_TAG)
                .setRecurring(true)
                .setTrigger(
                    Trigger.executionWindow(
                        prefRepository.servicesInterval * 60,
                        (prefRepository.servicesInterval + 10) * 60
                    )
                )
                .setConstraints(if (prefRepository.servicesOnlyWifi) Constraint.ON_UNMETERED_NETWORK else Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build()
        )
    }

    private fun stopFullSyncService() {
        dispatcher.cancel(SyncWorker.WORK_TAG)
    }
}
