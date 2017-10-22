package io.github.wulkanowy.activity.login;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.security.Safety;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;
import io.github.wulkanowy.services.jobs.GradesSync;

public class UserFirstLogin {

    private Context context;

    private Login login;

    private String email;

    private String password;

    private String symbol;

    private long userId;

    public UserFirstLogin(Context context, Login login, String email, String password, String symbol) {
        this.context = context;
        this.login = login;
        this.email = email;
        this.password = password;
        this.symbol = symbol;
    }

    public Login getLogin() {
        return login;
    }

    public String connect() throws BadCredentialsException, IOException {
        return login.sendCredentials(email, password, symbol);
    }

    public String sendCertificate(String certificate) throws IOException, NotLoggedInErrorException, AccountPermissionException {
        return login.sendCertificate(certificate, symbol);
    }

    public void login(DaoSession daoSession, Vulcan vulcan)
            throws AccountPermissionException, NotLoggedInErrorException,
            CryptoException, IOException {
        PersonalData personalData = vulcan.getBasicInformation().getPersonalData();

        AccountDao accountDao = daoSession.getAccountDao();
        Safety safety = new Safety();
        Account account = new Account()
                .setName(personalData.getFirstAndLastName())
                .setEmail(email)
                .setPassword(safety.encrypt(email, password, context))
                .setSymbol(vulcan.getStudentAndParent().getSymbol())
                .setSnpId(vulcan.getStudentAndParent().getId());

        userId = accountDao.insert(account);

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", userId);
        editor.apply();
    }

    public void setUpSynchronization(DaoSession daoSession, Vulcan vulcan) {
        VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession()
                .setVulcan(vulcan)
                .setUserId(userId)
                .setDaoSession(daoSession));

        vulcanSynchronization.syncSubjectsAndGrades();
    }

    public void scheduleSynchronization() {
        GradesSync gradesSync = new GradesSync();
        gradesSync.scheduledJob(context);
    }
}
