package io.github.wulkanowy.services.jobs;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import io.github.wulkanowy.services.JobHelper;

public class GradesSync extends JobHelper {

    @Override
    protected Job createJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(GradesSyncJob.class)
                .setTag(GradesSyncJob.UNIQUE_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(20, 30))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
    }
}
