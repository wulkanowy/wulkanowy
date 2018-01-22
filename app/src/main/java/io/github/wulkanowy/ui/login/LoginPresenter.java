package io.github.wulkanowy.ui.login;

import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.github.wulkanowy.R;
import io.github.wulkanowy.db.DatabaseManager;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.KeyboardUtils;

public class LoginPresenter extends BasePresenter<LoginActivity> {

    @Inject
    public LoginPresenter(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    void attemptLogin(String email, String password, String symbol) {
        getConnectedActivity().resetViewErrors();

        boolean cancel = false;

        if (TextUtils.isEmpty(password)) {
            getConnectedActivity().setPasswordError(R.string.error_field_required);
            getConnectedActivity().requestPasswordViewFocus();
            cancel = true;
        } else if (!isPasswordValid(password)) {
            getConnectedActivity().setPasswordError(R.string.error_invalid_password);
            getConnectedActivity().requestPasswordViewFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            getConnectedActivity().setEmailError(R.string.error_field_required);
            getConnectedActivity().requestEmailViewFocus();
            cancel = true;
        } else if (!isEmailValid(email)) {
            getConnectedActivity().setEmailError(R.string.error_invalid_email);
            getConnectedActivity().requestEmailViewFocus();
            cancel = true;
        }

        if (cancel) {
            return;
        }

        if (TextUtils.isEmpty(symbol)) {
            symbol = "Default";
        }

        String[] keys = getConnectedActivity().getResources().getStringArray(R.array.symbols);
        String[] values = getConnectedActivity().getResources().getStringArray(R.array.symbols_values);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        for (int i = 0; i < Math.min(keys.length, values.length); ++i) {
            map.put(keys[i], values[i]);
        }

        if (map.containsKey(symbol)) {
            symbol = map.get(symbol);
        }

        LoginTask authTask = new LoginTask(getConnectedActivity(), email, password, symbol);
        // authTask.showProgress(true);
        authTask.execute();
        KeyboardUtils.hideSoftInput(getConnectedActivity());

    }

    void openInternalBrowserViewer(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(getConnectedActivity().getResources().getColor(R.color.colorPrimary));
        customTabsIntent.launchUrl(getConnectedActivity(), Uri.parse(url));
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") || email.contains("\\\\");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
