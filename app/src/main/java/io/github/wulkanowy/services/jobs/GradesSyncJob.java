package io.github.wulkanowy.services.jobs;


import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import io.github.wulkanowy.services.JobHelper;
import io.github.wulkanowy.services.synchronisation.DataSynchronisation;
import io.github.wulkanowy.services.synchronisation.VulcanSynchronisation;

public class GradesSyncJob extends JobService {

    public static final String UNIQUE_TAG = "GradesSyncJob34512";

    public static final int DEFAULT_INTERVAL_START = 60 * 60;

    public static final int DEFAULT_INTERVAL_END = DEFAULT_INTERVAL_START + (5 * 60);

    private SyncTask syncTask = new SyncTask();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(JobHelper.DEBUG_TAG, "Start job");
        syncTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(JobHelper.DEBUG_TAG, "Stop job");
        syncTask.cancel(true);
        return true;
    }

    private class SyncTask extends AsyncTask<JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {
            DataSynchronisation dataSynchronisation = new DataSynchronisation(getApplicationContext());
            VulcanSynchronisation vulcanSynchronisation = new VulcanSynchronisation();
            try {
                vulcanSynchronisation.loginCurrentUser(getApplicationContext());
                dataSynchronisation.syncGrades(vulcanSynchronisation);
            } catch (Exception e) {
                Log.e(JobHelper.DEBUG_TAG, "User logging in the background failed", e);
            } finally {
                jobFinished(params[0], false);
            }

            return null;
        }
    }
}
