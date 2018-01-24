package io.github.wulkanowy.ui.login;

import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.github.wulkanowy.R;
import io.github.wulkanowy.db.DatabaseManager;
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
            getView().setPasswordError(R.string.error_field_required);
            getView().requestPasswordViewFocus();
            cancel = true;
        } else if (!isPasswordValid(password)) {
            getView().setPasswordError(R.string.error_invalid_password);
            getView().requestPasswordViewFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            getView().setEmailError(R.string.error_field_required);
            getView().requestEmailViewFocus();
            cancel = true;
        } else if (!isEmailValid(email)) {
            getView().setEmailError(R.string.error_invalid_email);
            getView().requestEmailViewFocus();
            cancel = true;
        }

        if (cancel) {
            return;
        }

        if (TextUtils.isEmpty(symbol)) {
            symbol = "Default";
        }

        //String[] keys = getView().getResources().getStringArray(R.array.symbols);
        //String[] values = getView().getResources().getStringArray(R.array.symbols_values);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        //for (int i = 0; i < Math.min(keys.length, values.length); ++i) {
        //  map.put(keys[i], values[i]);
        //}

        if (map.containsKey(symbol)) {
            symbol = map.get(symbol);
        }

        //LoginTask authTask = new LoginTask(getView(), email, password, symbol);
        // authTask.showProgress(true);
        //authTask.execute();
        //KeyboardUtils.hideSoftInput(getView());

    }

    public void openInternalBrowserViewer(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        //builder.setToolbarColor(getView().getResources().getColor(R.color.colorPrimary));
        //customTabsIntent.launchUrl(getView(), Uri.parse(url));
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") || email.contains("\\\\");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
