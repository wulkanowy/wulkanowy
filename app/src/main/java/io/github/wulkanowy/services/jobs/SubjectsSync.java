package io.github.wulkanowy.services.jobs;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import io.github.wulkanowy.services.JobHelper;

public class SubjectsSync extends JobHelper {

    @Override
    protected Job createJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setService(SubjectsSyncJob.class)
                .setTag(SubjectsSyncJob.UNIQUE_TAG)
                .setRecurring(false)
                .setTrigger(Trigger.executionWindow(SubjectsSyncJob.DEFAULT_INTERVAL_START, SubjectsSyncJob.DEFAULT_INTERVAL_END))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
    }
}
