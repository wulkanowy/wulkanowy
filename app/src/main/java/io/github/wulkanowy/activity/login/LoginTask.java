package io.github.wulkanowy.activity.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.utilities.ConnectionUtilities;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class LoginTask extends AsyncTask<Void, String, Integer> {

    private final String email;

    private final String password;

    private final String symbol;

    private Activity activity;

    private View progressView;

    private View loginFormView;

    private ProgressBar progressbar;
    private TextView showText;

    public LoginTask(Activity activity, String email, String password, String symbol) {
        this.activity = activity;
        this.email = email;
        this.password = password;
        this.symbol = symbol;
    }

    @Override
    protected void onPreExecute() {
        progressbar = activity.findViewById(R.id.login_progress_horizontal);
        showText = activity.findViewById(R.id.login_progress_text);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (ConnectionUtilities.isOnline(activity)) {
            LoginSteps loginSteps = new LoginSteps(activity, email, password, symbol);
            try {
                publishProgress("0", "Przygotowywanie");
                loginSteps.prepare();

                publishProgress("1", "Pobieranie certyfikatu");
                loginSteps.getCertificate();

                publishProgress("2", "Logowanie");
                loginSteps.login();

                publishProgress("3", "Pobieranie informacji o użytkowniku");
                loginSteps.getUserInfo();
                try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

                publishProgress("4", "Tworzenie lokalnego konta");
                loginSteps.createLocalAccount();
                try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

                publishProgress("5", "Ustawianie konta jako aktywnego");
                loginSteps.setUpAccountAsActive();
                try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

                publishProgress("6", "Ustawianie synchronizacji");
                loginSteps.setUpSynchronization();
                try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

            } catch (BadCredentialsException e) {
                return R.string.login_bad_credentials_text;
            } catch (AccountPermissionException e) {
                return R.string.error_bad_account_permission;
            } catch (CryptoException e) {
                return R.string.encrypt_failed_text;
            } catch (NotLoggedInErrorException | IOException e) {
                return R.string.login_denied_text;
            }

            publishProgress("7", "Synchronizacja ocen");
            loginSteps.synchronizeGrades();
            try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
            return R.string.login_accepted_text;

        } else {
            return R.string.noInternet_text;
        }
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        progressbar.setProgress(Integer.parseInt(progress[0]) * 10);
        showText.setText(progress[0] +"/7 - " + progress[1] + "...");
    }

    @Override
    protected void onPostExecute(final Integer messageID) {
        showProgress(false);

        switch (messageID) {
            // if success
            case R.string.login_accepted_text:
                Intent intent = new Intent(activity, DashboardActivity.class);
                activity.finish();
                activity.startActivity(intent);
                break;

            // if bad credentials entered
            case R.string.login_bad_credentials_text:
                EditText passwordView = activity.findViewById(R.id.password);
                passwordView.setError(activity.getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
                break;

            // if no permission
            case R.string.error_bad_account_permission:
                // Change to visible symbol input view
                TextInputLayout symbolLayout = activity.findViewById(R.id.to_symbol_input_layout);
                symbolLayout.setVisibility(View.VISIBLE);

                EditText symbolView = activity.findViewById(R.id.symbol);
                symbolView.setError(activity.getString(R.string.error_bad_account_permission));
                symbolView.requestFocus();
                break;

            default:
                Snackbar snackbar = Snackbar.make(
                        activity.findViewById(R.id.coordinatorLayout),
                        messageID, Snackbar.LENGTH_LONG
                );
                snackbar.show();
                break;
        }
    }

    @Override
    protected void onCancelled() {
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        loginFormView = activity.findViewById(R.id.login_form);
        progressView = activity.findViewById(R.id.login_progress);

        int animTime = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);

        changeLoginFormVisibility(show, animTime);
        changeProgressVisibility(show, animTime);
    }

    private void changeLoginFormVisibility(final boolean show, final int animTime) {
        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(animTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void changeProgressVisibility(final boolean show, final int animTime) {
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(animTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
