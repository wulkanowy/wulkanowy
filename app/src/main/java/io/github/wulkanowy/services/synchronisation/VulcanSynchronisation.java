package io.github.wulkanowy.services.synchronisation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.dao.Account;
import io.github.wulkanowy.dao.AccountDao;
import io.github.wulkanowy.dao.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.security.Safety;
import io.github.wulkanowy.services.jobs.VulcanSync;

public class VulcanSynchronisation {

    private StudentAndParent studentAndParent;

    public void loginCurrentUser(Context context, DaoSession daoSession) throws CryptoException,
            BadCredentialsException, LoginErrorException, AccountPermissionException, IOException {

        AccountDao accountDao = daoSession.getAccountDao();

        long userId = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("isLogin", 0);

        if (userId != 0) {

            Log.d(VulcanSync.DEBUG_TAG, "Login current user id=" + String.valueOf(userId));

            Safety safety = new Safety(context);
            Account account = accountDao.load(userId);
            Login login = loginUser(
                    account.getEmail(),
                    safety.decrypt(account.getEmail(), account.getPassword()),
                    account.getSymbol());

            getAndSetStudentAndParentFromApi(account.getSymbol(), login.getCookies());
        } else {
            Log.wtf(VulcanSync.DEBUG_TAG, "loginCurrentUser - USERID IS EMPTY");
        }
    }

    public void loginNewUser(String email, String password, String symbol, Context context, DaoSession daoSession)
            throws BadCredentialsException, LoginErrorException, AccountPermissionException, IOException, CryptoException {

        AccountDao accountDao = daoSession.getAccountDao();

        Login login = loginUser(email, password, symbol);

        Safety safety = new Safety(context);
        BasicInformation basicInformation = new BasicInformation(getAndSetStudentAndParentFromApi(symbol, login.getCookies()));
        PersonalData personalData = basicInformation.getPersonalData();

        Account account = new Account()
                .setName(personalData.getFirstAndLastName())
                .setEmail(email)
                .setPassword(safety.encrypt(email, password))
                .setSymbol(symbol);

        long idNewUser = accountDao.insert(account);

        Log.d(VulcanSync.DEBUG_TAG, "Login and save new user id=" + String.valueOf(idNewUser));

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("isLogin", idNewUser);
        editor.apply();
    }

    public void loginNewUser(String email, String password, String symbol, Activity activity)
            throws BadCredentialsException, LoginErrorException, AccountPermissionException, IOException, CryptoException {
        loginNewUser(email, password, symbol, activity, ((WulkanowyApp) activity.getApplication()).getDaoSession());
    }

    public StudentAndParent getStudentAndParent() {
        return studentAndParent;
    }

    private void setStudentAndParent(StudentAndParent studentAndParent) {
        this.studentAndParent = studentAndParent;
    }

    private Login loginUser(String email, String password, String symbol) throws BadCredentialsException,
            LoginErrorException, AccountPermissionException {

        Cookies cookies = new Cookies();
        Login login = new Login(cookies);
        login.login(email, password, symbol);
        return login;

    }

    private StudentAndParent getAndSetStudentAndParentFromApi(String symbol, Map<String, String> cookiesMap)
            throws IOException, LoginErrorException {

        if (studentAndParent == null) {
            Cookies cookies = new Cookies();
            cookies.setItems(cookiesMap);

            StudentAndParent snp = new StudentAndParent(cookies, symbol);

            setStudentAndParent(snp);
            return snp;
        } else {
            return studentAndParent;
        }
    }
}
