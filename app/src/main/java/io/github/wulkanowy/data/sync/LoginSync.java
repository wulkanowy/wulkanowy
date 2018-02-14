package io.github.wulkanowy.data.sync;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.utils.LogUtils;
import io.github.wulkanowy.utils.security.CryptoException;

@Singleton
public class LoginSync implements LoginSyncContract {

    private static final String DEBUG_TAG = "WulkanowyLoginSync";

    private final DaoSession daoSession;

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    @Inject
    LoginSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void loginUser(String email, String password, String symbol)
            throws NotLoggedInErrorException, AccountPermissionException, IOException,
            CryptoException, VulcanOfflineException, BadCredentialsException {

        LogUtils.debug(DEBUG_TAG, "Login new user email=" + email);

        vulcan.login(email, password, symbol);

        Account account = new Account()
                .setName(vulcan.getBasicInformation().getPersonalData().getFirstAndLastName())
                .setEmail(email)
                .setPassword(password)
                .setSymbol(symbol)
                .setSnpId(vulcan.getStudentAndParent().getId());

        sharedPref.setCurrentUserId(daoSession.getAccountDao().insert(account));
    }

    @Override
    public void loginCurrentUser() throws NotLoggedInErrorException, AccountPermissionException,
            IOException, CryptoException, VulcanOfflineException, BadCredentialsException {

        long userId = sharedPref.getCurrentUserId();

        if (userId == 0) {
            throw new IOException("Can't find logged user");
        }

        LogUtils.debug(DEBUG_TAG, "Login current user id=" + String.valueOf(userId));

        Account account = daoSession.getAccountDao().load(userId);

        vulcan.login(account.getEmail(),
                account.getPassword(),
                account.getSymbol(),
                account.getSnpId());
    }
}
