package io.github.wulkanowy.services;


import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class JobHelper {

    public static final String DEBUG_TAG = "SyncJob";

    public static void scheduledJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.mustSchedule(createJob(dispatcher));
    }

    private static Job createJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(SyncJob.class)
                .setTag(SyncJob.UNIQUE_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(SyncJob.DEFAULT_INTERVAL_START, SyncJob.DEFAULT_INTERVAL_END))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
    }
}
