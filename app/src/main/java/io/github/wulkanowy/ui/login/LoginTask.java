package io.github.wulkanowy.ui.login;

import android.os.AsyncTask;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class LoginTask extends AsyncTask<Void, Integer, Boolean> implements LoginContract.Task {

    private String email;

    private String password;

    private String symbol;

    private LoginContract.Presenter presenter;

    private Exception exception;

    @Override
    public void start(LoginContract.Presenter presenter, String email, String password, String symbol) {
        this.presenter = presenter;
        this.email = email;
        this.password = password;
        this.symbol = symbol;
        execute();
    }

    @Override
    public void onDestroy() {
        this.cancel(true);
    }

    @Override
    protected void onPreExecute() {
        presenter.onStartAsync();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            publishProgress(1);
            //vulcanSync.firstLoginSignInStep(activity.get(), daoSession, email, password, symbol);
            Thread.sleep(3000);

            publishProgress(2);
            //vulcanSync.syncAll();
            Thread.sleep(3000);
        } catch (Exception e) {
            exception = e;
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        presenter.onLoginProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        presenter.onEndAsync(success, exception);
    }

    @Override
    protected void onCancelled() {
        presenter.onCanceledAsync();
    }
}
