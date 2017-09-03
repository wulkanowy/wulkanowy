package io.github.wulkanowy.activity.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.services.SyncData;
import io.github.wulkanowy.services.SyncJob;

public class LoginTask extends AsyncTask<String, Integer, Integer> {

    private Context context;

    private ProgressDialog progress;

    private SyncData syncData;

    public LoginTask(Context context) {
        this.context = context;
        this.progress = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.setTitle(context.getText(R.string.login_text));
        progress.setMessage(context.getText(R.string.please_wait_text));
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected Integer doInBackground(String... credentials) {

        syncData = new SyncData(context);
        int messageId = syncData.loginNewUser(credentials[0], credentials[1], credentials[2]);
        syncData.syncGradesAndSubjects();
        return messageId;
    }

    protected void onPostExecute(Integer messageID) {
        super.onPostExecute(messageID);

        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job job = firebaseJobDispatcher.newJobBuilder()
                .setService(SyncJob.class)
                .setTag(SyncJob.UNIQE_TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(20, 30))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        firebaseJobDispatcher.mustSchedule(job);

        progress.dismiss();

        Toast.makeText(context, context.getString(messageID), Toast.LENGTH_LONG).show();

        if (messageID == R.string.login_accepted_text || messageID == R.string.root_failed_text
                || messageID == R.string.encrypt_failed_text) {
            Intent intent = new Intent(context, DashboardActivity.class);
            context.startActivity(intent);
        }
    }
}
