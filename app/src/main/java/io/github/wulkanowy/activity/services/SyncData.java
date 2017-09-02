package io.github.wulkanowy.activity.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.main.MainActivity;
import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.database.accounts.Account;
import io.github.wulkanowy.database.accounts.AccountsDatabase;
import io.github.wulkanowy.database.cookies.CookiesDatabase;
import io.github.wulkanowy.database.grades.GradesDatabase;
import io.github.wulkanowy.database.subjects.SubjectsDatabase;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.security.Safety;

public class SyncData {

    private Map<String, String> loginCookies = new HashMap<>();

    private Context context;

    private long userId;

    public SyncData(Context context) {
        this.context = context;
        userId = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("isLogin", 0);
    }

    public void syncGradesAndSubjects() {

        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                    .setPrettyPrinting().create();
            CookiesDatabase cookiesDatabase = new CookiesDatabase(context);
            cookiesDatabase.open();
            loginCookies = gson.fromJson(cookiesDatabase.getCookies(), loginCookies.getClass());
            cookiesDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Cookies cookies = new Cookies();
            cookies.setItems(loginCookies);

            AccountsDatabase accountsDatabase = new AccountsDatabase(context);
            accountsDatabase.open();
            Account account = accountsDatabase.getAccount(userId);
            accountsDatabase.close();

            StudentAndParent snp = new StudentAndParent(cookies, account.getSymbol());
            SubjectsList subjectsList = new SubjectsList(snp);

            SubjectsDatabase subjectsDatabase = new SubjectsDatabase(context);
            subjectsDatabase.open();
            subjectsDatabase.put(subjectsList.getAll());
            subjectsDatabase.close();


            GradesList gradesList = new GradesList(snp);
            GradesDatabase gradesDatabase = new GradesDatabase(context);
            gradesDatabase.open();
            gradesDatabase.put(gradesList.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int loginCurrentUser() throws NoTableException {

        AccountsDatabase accountsDatabase = new AccountsDatabase(context);
        accountsDatabase.open();

        if (accountsDatabase.checkExist("accounts")) {

            Account account = accountsDatabase.getAccount(userId);

            Cookies cookies = new Cookies();
            Login login = new Login(cookies);
            Safety safety = new Safety(context);

            try {
                login.login(
                        account.getEmail(),
                        safety.decrypt(account.getEmail(), account.getPassword()),
                        account.getSymbol()
                );
            } catch (BadCredentialsException e) {
                return R.string.login_bad_credentials_text;
            } catch (AccountPermissionException e) {
                return R.string.login_bad_account_permission_text;
            } catch (LoginErrorException e) {
                return R.string.login_denied_text;
            } catch (CryptoException e) {
                return R.string.decrypt_failed_text;
            } catch (SQLException e) {
                return R.string.SQLite_ioError_text;
            }

            try {
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                        .setPrettyPrinting().create();
                CookiesDatabase cookiesDatabase = new CookiesDatabase(context);
                cookiesDatabase.open();
                cookiesDatabase.put(gson.toJson(login.getCookies()));
                cookiesDatabase.close();
            } catch (SQLException e) {
                return R.string.login_cookies_save_failed_text;
            }

            return R.string.login_accepted_text;

        } else {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);

            throw new NoTableException();
        }

    }

    public int loginNewUser(String email, String password, String symbol) {

        Cookies cookies = new Cookies();
        Login login = new Login(cookies);
        Safety safety = new Safety(context);
        AccountsDatabase accountsDatabase = new AccountsDatabase(context);

        try {
            login.login(
                    email,
                    password,
                    symbol
            );
        } catch (BadCredentialsException e) {
            return R.string.login_bad_credentials_text;
        } catch (AccountPermissionException e) {
            return R.string.login_bad_account_permission_text;
        } catch (LoginErrorException e) {
            return R.string.login_denied_text;
        } catch (SQLException e) {
            return R.string.SQLite_ioError_text;
        }

        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                    .setPrettyPrinting().create();
            CookiesDatabase cookiesDatabase = new CookiesDatabase(context);
            cookiesDatabase.open();
            cookiesDatabase.put(gson.toJson(login.getCookies()));
            cookiesDatabase.close();
        } catch (SQLException e) {
            return R.string.login_cookies_save_failed_text;
        }

        try {
            StudentAndParent snp = new StudentAndParent(login.getCookiesObject(), symbol);
            BasicInformation userInfo = new BasicInformation(snp);
            PersonalData personalData = userInfo.getPersonalData();
            String firstAndLastName = personalData.getFirstAndLastName();

            Account account = new Account()
                    .setName(firstAndLastName)
                    .setEmail(email)
                    .setPassword(safety.encrypt(email, password))
                    .setSymbol(symbol);

            accountsDatabase.open();
            long idUser = accountsDatabase.put(account);
            accountsDatabase.close();

            SharedPreferences sharedPreferences = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("isLogin", idUser);
            editor.apply();

        } catch (SQLException e) {
            return R.string.SQLite_ioError_text;
        } catch (IOException | LoginErrorException e) {
            return R.string.login_denied_text;
        } catch (CryptoException e) {
            return R.string.encrypt_failed_text;
        } catch (UnsupportedOperationException e) {
            return R.string.root_failed_text;
        }

        return R.string.login_accepted_text;
    }
}
