package io.github.wulkanowy.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.github.wulkanowy.R;
import io.github.wulkanowy.ui.base.BaseActivity;
import io.github.wulkanowy.utils.AppConstant;
import io.github.wulkanowy.utils.CommonUtils;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R.id.email)
    EditText emailView;

    @BindView(R.id.password)
    EditText passwordView;

    @BindView(R.id.symbol)
    AutoCompleteTextView symbolView;

    @BindView(R.id.login_form)
    View loginFormView;

    @BindView(R.id.login_progress)
    View loadingBarView;

    @BindView(R.id.login_progress_text)
    TextView showText;

    @Inject
    LoginContract.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setButterKnife(ButterKnife.bind(this));

        getActivityComponent().inject(this);

        initiationAutoComplete();

        presenter.onStart(this);

    }

    @OnClick(R.id.action_sign_in)
    void onLoginButtonClick() {
        presenter.attemptLogin(
                emailView.getText().toString(),
                passwordView.getText().toString(),
                symbolView.getText().toString());
    }

    @OnEditorAction(value = {R.id.symbol, R.id.password})
    boolean onEditorAction(int id) {
        if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
            onLoginButtonClick();
            return true;
        }
        return false;
    }

    @OnClick(R.id.action_create_account)
    void onCreateAccountButtonClick() {
        CommonUtils.openInternalBrowserViewer(getApplicationContext(),
                AppConstant.VULCAN_CREATE_ACCOUNT_URL);
    }

    @OnClick(R.id.action_forgot_password)
    void onForgotPasswordButtonClick() {
        CommonUtils.openInternalBrowserViewer(getApplicationContext(),
                AppConstant.VULCAN_FORGOT_PASS_URL);
    }

    @Override
    public void requestPasswordViewFocus() {
        passwordView.requestFocus();
    }

    @Override
    public void requestEmailViewFocus() {
        emailView.requestFocus();
    }

    @Override
    public void setPasswordError(String message) {
        passwordView.setError(message);
    }

    @Override
    public void setEmailError(String message) {
        emailView.setError(message);
    }

    private void initiationAutoComplete() {
        symbolView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.symbols)));
    }

    @Override
    public void resetViewErrors() {
        emailView.setError(null);
        passwordView.setError(null);
    }

    void onLoginProgressUpdate(String... progress) {
        showText.setText(String.format("%1$s/2 - %2$s...", progress[0], progress[1]));
    }

    void showLoginProgress(final boolean show) {
        int animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(animTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        loadingBarView.setVisibility(show ? View.VISIBLE : View.GONE);
        loadingBarView.animate().setDuration(animTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loadingBarView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
