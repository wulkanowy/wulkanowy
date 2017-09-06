package io.github.wulkanowy.services;


import android.app.NotificationManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class SyncJob extends JobService {

    public static final String UNIQE_TAG = "SyncJob12345";

    public static final int DEFAULT_INTERVAL_START = 60 * 60;

    public static final int DEFAULT_INTERVAL_END = DEFAULT_INTERVAL_START + (5 * 60);

    private SyncTask syncTask = new SyncTask();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(JobHelper.DEBUG_TAG, "Start job");
        sendNotification();
        syncTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(JobHelper.DEBUG_TAG, "Stop job");
        syncTask.cancel(true);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("DEBUG NOTIFICATION ")
                .setContentText("DEBUG TEST")
                .setSmallIcon(android.R.drawable.ic_dialog_alert);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(123, builder.build());
    }

    private class SyncTask extends AsyncTask<JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {
            SyncData syncData = new SyncData(getApplicationContext());
            try {
                syncData.loginCurrentUser();
                syncData.syncGradesAndSubjects();
            } catch (NoTableException e) {
                Log.d(JobHelper.DEBUG_TAG, "No table account");
            } finally {
                jobFinished(params[0], false);
            }

            return null;
        }
    }
}
