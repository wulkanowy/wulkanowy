package io.github.wulkanowy.ui.login;

import android.text.TextUtils;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.db.resources.AppResources;
import io.github.wulkanowy.ui.base.BasePresenter;

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    @Inject
    public LoginPresenter(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public void attemptLogin(String email, String password, String symbol) {
        getView().resetViewErrors();

        boolean cancel = false;

        if (TextUtils.isEmpty(password)) {

            cancel = true;
        } else if (!isPasswordValid(password)) {
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            cancel = true;
        } else if (!isEmailValid(email)) {
            cancel = true;
        }

        if (cancel) {
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

        } else {

        }
        //LoginTask authTask = new LoginTask(getView(), email, password, symbol);
        // authTask.showProgress(true);
        //authTask.execute();
        getView().hideSoftKeyboard();

    }

    private boolean isEmailValid(String email) {
        return email.contains("@") || email.contains("\\\\");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
