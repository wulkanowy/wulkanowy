package io.github.wulkanowy.activity.splash;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.activity.login.LoginActivity;
import io.github.wulkanowy.services.jobs.GradeJob;
import io.github.wulkanowy.utilities.ConnectionUtilities;

public class LoadingTask extends AsyncTask<Void, Void, Boolean> {

    private WeakReference<Context> weakContext;

    LoadingTask(Context context) {
        this.weakContext = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ConnectionUtilities.isOnline(weakContext.get());
    }

    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        Context context = weakContext.get();

        if (!result) {
            Toast.makeText(context, R.string.noInternet_text, Toast.LENGTH_LONG).show();
        }

        if (context.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("userId", 0) == 0) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        } else {
            GradeJob gradesSync = new GradeJob();
            gradesSync.scheduledJob(context);

            Intent intent = new Intent(context, DashboardActivity.class);
            context.startActivity(intent);
        }

    }
}
