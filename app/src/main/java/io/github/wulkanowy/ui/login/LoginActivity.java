package io.github.wulkanowy.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.StringRes;
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

public class LoginActivity extends BaseActivity {

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
    LoginPresenter presenter;

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
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String symbol = symbolView.getText().toString();

        presenter.attemptLogin(email, password, symbol);
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
        presenter.openInternalBrowserViewer(
                "https://cufs.vulcan.net.pl/Default/AccountManage/CreateAccount"
        );
    }

    @OnClick(R.id.action_forgot_password)
    void onForgotPasswordButtonClick() {
        presenter.openInternalBrowserViewer(
                "https://cufs.vulcan.net.pl/Default/AccountManage/UnlockAccount"
        );
    }

    void requestPasswordViewFocus() {
        passwordView.requestFocus();
    }

    void requestEmailViewFocus() {
        emailView.requestFocus();
    }

    void setPasswordError(@StringRes int stringId) {
        passwordView.setError(getString(stringId));
    }

    void setEmailError(@StringRes int stringId) {
        emailView.setError(getString(stringId));
    }

    private void initiationAutoComplete() {
        symbolView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.symbols)));
    }

    void resetViewErrors() {
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
