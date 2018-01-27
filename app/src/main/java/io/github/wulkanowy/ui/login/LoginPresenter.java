package io.github.wulkanowy.ui.login;

import android.text.TextUtils;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.db.resources.AppResources;
import io.github.wulkanowy.ui.base.BasePresenter;

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    private LoginContract.Task loginAsync;

    @Inject
    public LoginPresenter(DatabaseManager databaseManager, LoginContract.Task loginAsync) {
        super(databaseManager);
        this.loginAsync = loginAsync;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginAsync != null) {
            loginAsync.onDestroy();
        }
    }

    public void attemptLogin(String email, String password, String symbol) {
        getView().resetViewErrors();

        boolean cancel = false;

        if (TextUtils.isEmpty(password)) {
            getView().setErrorPassRequired();
            cancel = true;
        } else if (!isPasswordValid(password)) {
            getView().setErrorPassInvalid();
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            getView().setErrorEmailRequired();
            cancel = true;
        } else if (!isEmailValid(email)) {
            getView().setErrorEmailInvalid();
            cancel = true;
        }

        if (cancel) {
            getView().showSoftInput();
            return;
        }

        if (TextUtils.isEmpty(symbol)) {
            symbol = "Default";
        }

        AppResources appResources = getDatabaseManager().getAppResources();

        String[] keys = appResources.getSymbolsKeysArray();
        String[] values = appResources.getSymbolsValuesArray();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        for (int i = 0; i < Math.min(keys.length, values.length); ++i) {
            map.put(keys[i], values[i]);
        }

        if (map.containsKey(symbol)) {
            symbol = map.get(symbol);
        }

        if (getView().isNetworkConnected()) {
            loginAsync.start(this, email, password, symbol);
        } else {
            getView().onNoNetworkError();
        }

        getView().hideSoftInput();
    }

    @Override
    public void onStartAsync() {
        getView().showLoginProgress(true);
    }

    @Override
    public void onLoginProgress(int step) {
        if (step == 1) {
            getView().setStepOneLoginProgress();
        } else if (step == 2) {
            getView().setStepTwoLoginProgress();
        }
    }

    @Override
    public void onEndAsync(boolean success, Exception exception) {
        getView().showLoginProgress(false);
        if (success) {
            //getView().openDashboardActivity();
            getView().onError("SUCCESS");
        } else if (exception instanceof BadCredentialsException) {
            getView().setErrorPassIncorrect();
            getView().showSoftInput();
        } else if (exception instanceof AccountPermissionException) {
            getView().setErrorSymbolRequired();
            getView().showSoftInput();
        } else {
            getView().onError(getDatabaseManager().getAppResources()
                    .getErrorLoginMessage(exception));
        }
    }

    @Override
    public void onCanceledAsync() {
        getView().showLoginProgress(false);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") || email.contains("\\\\");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
