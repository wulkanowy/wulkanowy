package io.github.wulkanowy.ui.login;

import android.text.TextUtils;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.db.resources.ResourcesHelper;
import io.github.wulkanowy.ui.base.BasePresenter;

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    @Inject
    public LoginPresenter(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public void attemptLogin(String email, String password, String symbol) {
        getView().resetViewErrors();

        ResourcesHelper resources = getDatabaseManager().getAppResources();

        boolean cancel = false;

        if (TextUtils.isEmpty(password)) {
            getView().setPasswordError(resources.getErrorFieldRequired());
            getView().requestPasswordViewFocus();
            cancel = true;
        } else if (!isPasswordValid(password)) {
            getView().setPasswordError(resources.getErrorPassInvalid());
            getView().requestPasswordViewFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            getView().setEmailError(resources.getErrorFieldRequired());
            getView().requestEmailViewFocus();
            cancel = true;
        } else if (!isEmailValid(email)) {
            getView().setEmailError(resources.getErrorEmailInvalid());
            getView().requestEmailViewFocus();
            cancel = true;
        }

        if (cancel) {
            return;
        }

        if (TextUtils.isEmpty(symbol)) {
            symbol = "Default";
        }

        String[] keys = resources.getSymbolsKeysArray();
        String[] values = resources.getSymbolsValuesArray();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        for (int i = 0; i < Math.min(keys.length, values.length); ++i) {
            map.put(keys[i], values[i]);
        }

        if (map.containsKey(symbol)) {
            symbol = map.get(symbol);
        }

        //LoginTask authTask = new LoginTask(getView(), email, password, symbol);
        // authTask.showProgress(true);
        //authTask.execute();
        //KeyboardUtils.hideSoftInput(getView());

    }

    private boolean isEmailValid(String email) {
        return email.contains("@") || email.contains("\\\\");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
