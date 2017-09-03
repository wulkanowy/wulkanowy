package io.github.wulkanowy.activity.started;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.activity.main.MainActivity;
import io.github.wulkanowy.services.NoTableException;
import io.github.wulkanowy.services.SyncData;
import io.github.wulkanowy.services.SyncJob;

public class LoadingTask extends AsyncTask<Void, Void, Integer> {

    private Context context;

    private ProgressDialog progress;

    LoadingTask(Context context) {
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
    protected Integer doInBackground(Void... voids) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isOnline()) {

            SyncData syncData = new SyncData(context);

            try {
                return syncData.loginCurrentUser();
            } catch (NoTableException e) {
                Log.d("SignIn", "No table accounts");
                return 0;
            }
        } else {
            return -1;
        }
    }

    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job job = firebaseJobDispatcher.newJobBuilder()
                .setService(SyncJob.class)
                .setTag(SyncJob.UNIQE_TAG)
                .setRecurring(false)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(20, 30))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        firebaseJobDispatcher.mustSchedule(job);

        progress.dismiss();

        if (result == -1) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);

            Toast.makeText(context, R.string.noInternet_text, Toast.LENGTH_SHORT).show();
        } else if (result != 0) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

            if (result == R.string.login_accepted_text || result == R.string.root_failed_text) {
                Intent intent = new Intent(context, DashboardActivity.class);
                context.startActivity(intent);
            }
        }
    }

    private boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress address = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(address, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
