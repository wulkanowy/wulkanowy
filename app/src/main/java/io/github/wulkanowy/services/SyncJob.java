package io.github.wulkanowy.services;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import javax.inject.Inject;

import io.github.wulkanowy.WulkanowyApp;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.utils.LogUtils;

public class SyncJob extends JobService {

    private static final int DEFAULT_INTERVAL_START = 60 * 50;

    private static final int DEFAULT_INTERVAL_END = DEFAULT_INTERVAL_START + (60 * 40);

    private Thread work;

    @Inject
    FirebaseJobDispatcher dispatcher;

    @Inject
    RepositoryContract repository;

    public void start() {
        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(SyncJob.class)
                .setTag(getClass().getName())
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(DEFAULT_INTERVAL_START, DEFAULT_INTERVAL_END))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((WulkanowyApp) getApplication()).getApplicationComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        work = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    repository.loginCurrentUser();
                    repository.syncAll();
                } catch (Exception e) {
                    LogUtils.error("During background synchronization an error occurred", e);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        work.interrupt();
        return true;
    }
}
