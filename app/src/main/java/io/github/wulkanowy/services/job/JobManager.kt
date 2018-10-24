package io.github.wulkanowy.services.job

import android.content.Context
import com.firebase.jobdispatcher.Constraint.ON_ANY_NETWORK
import com.firebase.jobdispatcher.Constraint.ON_UNMETERED_NETWORK
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.RetryStrategy.DEFAULT_EXPONENTIAL
import com.firebase.jobdispatcher.Trigger.executionWindow
import timber.log.Timber

class JobManager {

    fun start(context: Context, interval: Int, useOnlyWifi: Boolean) {
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))

        dispatcher.mustSchedule(
            dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(SyncWorker::class.java)
                .setTag(SyncWorker.WORK_TAG)
                .setRecurring(true)
                .setTrigger(executionWindow(interval * 60, (interval + 10) * 60))
                .setConstraints(if (useOnlyWifi) ON_UNMETERED_NETWORK else ON_ANY_NETWORK)
                .setReplaceCurrent(false)
                .setRetryStrategy(DEFAULT_EXPONENTIAL)
                .build()
        )

        Timber.d("Sync work scheduled")
    }

    fun stop(context: Context) {
        FirebaseJobDispatcher(GooglePlayDriver(context)).cancel(SyncWorker.WORK_TAG)
    }
}
