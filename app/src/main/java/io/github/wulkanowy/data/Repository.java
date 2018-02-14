package io.github.wulkanowy.data;


import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.LoginSyncContract;
import io.github.wulkanowy.utils.security.CryptoException;

@Singleton
public class Repository implements RepositoryContract {

    private final SharedPrefContract sharedPref;

    private final ResourcesContract resources;

    private final LoginSyncContract loginSync;

    @Inject
    public Repository(SharedPrefContract sharedPref,
                      ResourcesContract resources,
                      LoginSyncContract loginSync) {
        this.sharedPref = sharedPref;
        this.resources = resources;
        this.loginSync = loginSync;
    }

    @Override
    public long getCurrentUserId() {
        return sharedPref.getCurrentUserId();
    }

    @Override
    public String[] getSymbolsKeysArray() {
        return resources.getSymbolsKeysArray();
    }

    @Override
    public String[] getSymbolsValuesArray() {
        return resources.getSymbolsValuesArray();
    }

    @Override
    public String getErrorLoginMessage(Exception e) {
        return resources.getErrorLoginMessage(e);
    }

    @Override
    public void loginUser(String email, String password, String symbol)
            throws NotLoggedInErrorException, AccountPermissionException, IOException,
            CryptoException, VulcanOfflineException, BadCredentialsException {
        loginSync.loginUser(email, password, symbol);
    }

    @Override
    public void loginCurrentUser() throws NotLoggedInErrorException, AccountPermissionException,
            IOException, CryptoException, VulcanOfflineException, BadCredentialsException {
        loginSync.loginCurrentUser();
    }
}
