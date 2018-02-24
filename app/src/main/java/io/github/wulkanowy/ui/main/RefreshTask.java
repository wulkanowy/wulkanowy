package io.github.wulkanowy.ui.main;

import android.os.AsyncTask;

public class RefreshTask extends AsyncTask<Void, Integer, Boolean> {

    private RefreshCallback callback;

    private Exception exception;

    public RefreshTask(RefreshCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            callback.onDoInBackground();
            return true;
        } catch (Exception e) {
            exception = e;
            return false;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        callback.onCanceledAsync();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        callback.onEndAsync(result, exception);
    }
}
