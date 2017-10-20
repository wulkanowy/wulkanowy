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
import io.github.wulkanowy.api.login.LoginErrorException;
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
import io.github.wulkanowy.services.jobs.VulcanSync;

public class LoginSteps {

    private Activity activity;

    private String email;

    private String password;

    private String symbol;

    private DaoSession daoSession;

    private Login login;

    private String certificate;

    private String realSymbol;

    private PersonalData personalData;

    private long userId;

    private StudentAndParent snp;

    private VulcanSynchronization vulcanSynchronization;

    public LoginSteps(Activity activity, String email, String password, String symbol) {
        this.activity = activity;
        this.email = email;
        this.password = password;
        this.symbol = symbol;
    }

    public void prepare() {
        daoSession = ((WulkanowyApp) activity.getApplication()).getDaoSession();
        login = new Login(new Cookies());
    }

    public void getCertificate() throws BadCredentialsException, IOException {
        certificate = login.sendCredentials(email, password, symbol);
    }

    public void login() throws AccountPermissionException, LoginErrorException, IOException {
        realSymbol = login.sendCertificate(certificate, symbol);
    }

    public void getUserInfo() throws NotLoggedInErrorException, IOException {
        snp = new StudentAndParent(login.getCookiesObject(), realSymbol);
        snp.storeContextCookies();
        personalData = new BasicInformation(snp).getPersonalData();
    }

    public void createLocalAccount() throws CryptoException {
        AccountDao accountDao = daoSession.getAccountDao();
        Safety safety = new Safety();
        Account account = new Account()
                .setName(personalData.getFirstAndLastName())
                .setEmail(email)
                .setPassword(safety.encrypt(email, password, activity))
                .setSymbol(realSymbol)
                .setSnpId(snp.getId());

        userId = accountDao.insert(account);

        Log.d(VulcanSync.DEBUG_TAG, "Login and save new user id=" + String.valueOf(userId));
    }

    public void setUpAccountAsActive() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", userId);
        editor.apply();
    }

    public void setUpSynchronization() {
        Vulcan vulcan = new Vulcan();
        vulcan.setCookies(snp.getCookies());
        vulcan.setSymbol(snp.getSymbol());
        vulcan.setId(snp.getId());
        vulcanSynchronization = new VulcanSynchronization(new LoginSession()
                .setVulcan(vulcan)
                .setUserId(userId)
                .setDaoSession(daoSession));
    }

    public void synchronizeGrades() {
        vulcanSynchronization.syncSubjectsAndGrades();
        GradesSync gradesSync = new GradesSync();
        gradesSync.scheduledJob(activity);
    }
}
