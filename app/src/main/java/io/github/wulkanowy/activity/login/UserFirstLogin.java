package io.github.wulkanowy.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.user.BasicInformation;
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

    private Activity activity;

    private String email;

    private String password;

    private String symbol;

    private DaoSession daoSession;

    private Login login;

    private String certificate;

    private long userId;

    private StudentAndParent snp;

    public UserFirstLogin(Activity activity, String email, String password, String symbol) {
        this.activity = activity;
        this.email = email;
        this.password = password;
        this.symbol = symbol;
    }

    public void connect() throws BadCredentialsException, IOException {
        daoSession = ((WulkanowyApp) activity.getApplication()).getDaoSession();
        login = new Login(new Cookies());
        certificate = login.sendCredentials(email, password, symbol);

        Log.d("UserFirstLogin", "Connected successfully");
    }

    public void login() throws AccountPermissionException, NotLoggedInErrorException,
            CryptoException, IOException {
        String realSymbol = login.sendCertificate(certificate, symbol);

        Log.d("UserFirstLogin", "Certificate sent");

        snp = new StudentAndParent(login.getCookiesObject(), realSymbol);
        snp.storeContextCookies();
        PersonalData personalData = new BasicInformation(snp).getPersonalData();

        Log.d("UserFirstLogin", "Successfully getting user first name");

        AccountDao accountDao = daoSession.getAccountDao();
        Safety safety = new Safety();
        Account account = new Account()
                .setName(personalData.getFirstAndLastName())
                .setEmail(email)
                .setPassword(safety.encrypt(email, password, activity))
                .setSymbol(realSymbol)
                .setSnpId(snp.getId());

        userId = accountDao.insert(account);

        Log.d("UserFirstLogin", "Local user id = " + String.valueOf(userId));

        SharedPreferences sharedPreferences = activity.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", userId);
        editor.apply();

        Log.d("UserFirstLogin", "Successfully setting user as active");
    }

    public void setUpSynchronization() {
        Vulcan vulcan = new Vulcan();
        vulcan.setCookies(snp.getCookies());
        vulcan.setSymbol(snp.getSymbol());
        vulcan.setId(snp.getId());

        VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession()
                .setVulcan(vulcan)
                .setUserId(userId)
                .setDaoSession(daoSession));

        vulcanSynchronization.syncSubjectsAndGrades();

        Log.d("UserFirstLogin", "First synchronization ended successfully");
    }

    public void scheduleSynchronization() {
        GradesSync gradesSync = new GradesSync();
        gradesSync.scheduledJob(activity);
    }
}
