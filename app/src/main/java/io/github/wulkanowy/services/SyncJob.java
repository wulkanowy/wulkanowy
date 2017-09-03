package io.github.wulkanowy.services;


import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class SyncJob extends JobService {

    public static final String DEBUG_TAG = "SyncJob";

    public static final String UNIQE_TAG = "SyncJob12345";

    public static final int DEFAULT_INTERVAL_START = 60 * 60;

    public static final int DEFAULT_INTERVAL_END = DEFAULT_INTERVAL_START + (5 * 60);

    SyncTask syncTask = new SyncTask();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(DEBUG_TAG, "Start job");
        syncTask.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        syncTask.cancel(true);
        return false;
    }

    public class SyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SyncData syncData = new SyncData(getApplicationContext());
            try {
                syncData.loginCurrentUser();
            } catch (NoTableException e) {
                Log.d(DEBUG_TAG, "No table account");
            }
            syncData.syncGradesAndSubjects();
            return null;
        }
    }
}
